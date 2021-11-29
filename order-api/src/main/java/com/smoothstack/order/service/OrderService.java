package com.smoothstack.order.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.database.ormlibrary.driver.DriverEntity;
import com.database.ormlibrary.food.MenuItemEntity;
import com.database.ormlibrary.order.*;
import com.database.ormlibrary.user.UserEntity;
import com.smoothstack.order.exception.OrderTimeException;
import com.smoothstack.order.exception.UserNotFoundException;
import com.smoothstack.order.exception.ValueNotPresentException;
import com.smoothstack.order.model.*;
import com.smoothstack.order.repo.*;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Refund;
import com.stripe.param.ChargeCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j(topic = "Order Service Logs")
public class OrderService {

    private final OrderRepo orderRepo;
    private final DriverRepo driverRepo;
    private final MenuItemRepo menuItemRepo;
    private final FoodOrderRepo foodOrderRepo;
    private final RestaurantRepo restaurantRepo;
    private final UserRepo userRepo;
    private final AmazonSimpleEmailService emailService;

    @Value("${email.sender}")
    private String emailFrom;
    @Value("${stripe.key}")
    private String stripeKey;
    private final String TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private final ModelMapper modelMapper;

    public OrderService(OrderRepo orderRepo, DriverRepo driverRepo,
                        MenuItemRepo menuItemRepo, FoodOrderRepo foodOrderRepo,
                        RestaurantRepo restaurantRepo, UserRepo userRepo,
                        AmazonSimpleEmailService emailService) {
        this.orderRepo = orderRepo;
        this.driverRepo = driverRepo;
        this.menuItemRepo = menuItemRepo;
        this.foodOrderRepo = foodOrderRepo;
        this.restaurantRepo = restaurantRepo;
        this.userRepo = userRepo;
        this.emailService = emailService;
        this.modelMapper = new ModelMapper();
    }

    public void deleteOrder(Long id) {
        OrderEntity orderEntity = orderRepo.findById(id).isPresent() ? orderRepo.findById(id).get() : null;
        if (orderEntity == null) {
            log.info("Delete Order: no order found to delete");
            new ResponseEntity<>(HttpStatus.OK);
            return;
        }
        List<FoodOrderEntity> foodOrderEntityList = orderEntity.getItems();
        foodOrderRepo.deleteAll(foodOrderEntityList);
        orderRepo.deleteById(id);
        log.info("Delete Order: Order deleted");
    }

    /**
     * Section for updating existing orders
     */
    public Void patchOrderStatus(Order orderDTO) {
        OrderEntity orderEntity = orderRepo.findById(orderDTO.getId()).orElse(null);
        if (orderEntity != null) {
            orderEntity.getOrderTimeEntity().setOrderComplete(ZonedDateTime.now());
            orderRepo.save(orderEntity);
            log.info("Order status patched");
        }
        return null;
    }

    public ResponseEntity<Void> patchOrders(Order order) {
        OrderEntity orderEntity = convertToEntity(order);
        orderRepo.save(orderEntity);
        log.info("patchOrders: order updated");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<String> patchOrder(Order order, Long userId) throws ValueNotPresentException {
        OrderEntity orderEntity = orderRepo.findById(order.getId()).orElseThrow(() ->
                new ValueNotPresentException("Order not found"));
        if (!Objects.equals(orderEntity.getUser().getId(), userId))
            return ResponseEntity.status(401).body("Order does not belong to this user");

        orderEntity.setRestaurantNote(order.getRestaurantNote());
        orderEntity.setDriverNote(order.getDriverNote());
        orderRepo.save(orderEntity);
        log.info("patchOrders: order updated");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<String> cancelOrder(Long orderId, Long userId) throws ValueNotPresentException {
        OrderEntity orderEntity = orderRepo.findById(orderId).orElseThrow(() ->
                new ValueNotPresentException("Order not found"));
        if (!Objects.equals(orderEntity.getUser().getId(), userId))
            return ResponseEntity.status(401).body("Order does not belong to this user");

        orderEntity.setRecordId(userId);

        UserEntity userEntity = orderEntity.getUser();

        orderEntity.setUser(null);
        orderRepo.save(orderEntity);

        sendOrderCancellationConfirmation(orderEntity, userEntity);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<CreateResponse> createOrder(Order orderDTO, Long userId, String chargeId)
            throws OrderTimeException, UserNotFoundException {
        OrderEntity orderEntity = convertToEntity(orderDTO);
        orderEntity.setActive(true);
        if (userRepo.findById(userId).isPresent())
            orderEntity.setUser(userRepo.findById(userId).get());
        else {
            throw new UserNotFoundException("User not found");
        }
        if (orderEntity.getDelivery()) {
            ZonedDateTime deliverySlot = orderEntity.getOrderTimeEntity().getDeliverySlot();
            ZonedDateTime placed = orderEntity.getOrderTimeEntity().getPlaced();
            long diff = ChronoUnit.MINUTES.between(placed, deliverySlot);
            if (diff < 15) throw new OrderTimeException("Time slot too early");
        }

        orderEntity.setChargeId(chargeId);
        orderEntity = orderRepo.save(orderEntity);
        log.info("Order created, id: " + orderEntity.getId());

        if (orderEntity.getUser().getSettings().getNotifications().getEmailOrder()){
            sendOrderConfirmation(orderEntity.getId(), orderEntity.getUser().getId());
        }
        return ResponseEntity.ok(new CreateResponse().type(CreateResponse.TypeEnum.STRIPE)
                .id(String.valueOf(orderEntity.getId())).setAddress(orderEntity.getAddress()));
    }

    public List<Order> getUserOrders(Long userId) throws UserNotFoundException {
        Optional<UserEntity> userEntityOptional = userRepo.findById(userId);
        if (userEntityOptional.isPresent()) {
            List<Order> orderList = new ArrayList<>();
            orderRepo.getOrderByUser(userId).forEach(orderEntity -> orderList.add(convertToDTO(orderEntity)));
            log.info("User order list retrieved");
            return orderList;
        }
        throw new UserNotFoundException("User not found!");
    }

    public ResponseEntity<Order> getOrder(Long id) throws ValueNotPresentException {
        Order orderDTO = orderRepo.findById(id).isPresent() ? convertToDTO(orderRepo.findById(id).get()) : null;
        if (orderDTO == null) {
            throw new ValueNotPresentException("No item of that id in the database");
        }
        log.info("getOrder: order found and returned");
        return ResponseEntity.ok(orderDTO);
    }

    public ResponseEntity<List<Order>> getActiveOrders(String sortType, Integer page, Integer size) {
        Iterable<OrderEntity> orderEntities = orderRepo.findAll();
        List<Order> orderDTOs = new ArrayList<>();
        for (OrderEntity orderEntity : orderEntities) {
            if (orderEntity.getRefunded() == null || !orderEntity.getRefunded())
                if (orderEntity.getDriver() == null) {
                    orderDTOs.add(convertToDTO(orderEntity));
                }
        }

        orderDTOs = sortList(orderDTOs, sortType);
        orderDTOs = pageList(orderDTOs, page, size);
        log.info("getActiveOrders: active orders retrieved");
        return ResponseEntity.ok(orderDTOs);
    }

    public List<Order> pageList(List<Order> orders, Integer page, Integer size) {
        if ((page + 1) * size > orders.size()) {
            return orders.subList(page * size, orders.size());
        }
        return orders.subList(page * size, (page + 1) * size);
    }

    public List<Order> sortList(List<Order> orders, String sortType) {
        switch (sortType != null ? sortType : "") {
            case "time":
                orders = orders.stream().sorted((x, y) -> sortTime(y.getOrderTime().getDeliverySlot(), x.getOrderTime().getDeliverySlot())).collect(Collectors.toList());
                break;
            case "price":
                orders = orders.stream().sorted((x, y) -> sortPrice(y.getPrice().getTip(), x.getPrice().getTip())).collect(Collectors.toList());
                break;
            default:
                break;
        }
        return orders;
    }

    public Integer sortPrice(BigDecimal price1, BigDecimal price2) {
        if (price1 == null) {
            return -1;
        }
        if (price2 == null) {
            return 1;
        }
        return price1.compareTo(price2);
    }

    public Integer sortTime(String time1, String time2) {
        if (time1 == null) {
            return -1;
        }
        if (time2 == null) {
            return 1;
        }
        ZonedDateTime t1 = ZonedDateTime.parse(time1);
        ZonedDateTime t2 = ZonedDateTime.parse(time2);
        return t1.toInstant().compareTo(t2.toInstant());
    }

    /*
    Section for emailing order changes
     */

    public Order sendOrderConfirmation(Long orderId, Long userId) throws UserNotFoundException {
        StringBuilder builder = new StringBuilder();
        Optional<UserEntity> userEntityOptional = userRepo.findById(userId);

        //Check if user is present
        if (userEntityOptional.isPresent()) {
            UserEntity entity = userEntityOptional.get();

            //If user opted for email notifications, start building email body
            if (entity.getSettings().getNotifications().getEmailOrder()) {
                log.info("senOrderConfirmation: user email order details option active");
                String emailTo = entity.getEmail();
                Order order = convertToDTO(orderRepo.findById(orderId).orElse(null));
                builder.append("<h1>Scrumptious Order Confirmation: <h1> \n <strong>Order Items: </strong>");
                String htmlBody = buildOrderDetails(builder, order).toString();
                //Sends the email
                sendEmail(emailTo, htmlBody);

                return order;
            }
            return null;
        }
        throw new UserNotFoundException("User not found!");
    }

    private void sendOrderCancellationConfirmation(OrderEntity orderEntity, UserEntity userEntity) {
        StringBuilder builder = new StringBuilder();

        if (userEntity.getSettings().getNotifications().getEmailOrder()) {
            log.info("sendOrderCancellationConfirmation: user email order details option active");
            Order order = convertToDTO(orderEntity);
            builder.append("<h1>Scrumptious Order Cancellation: <h1> \n <strong>Order Items: </strong>");
            buildOrderDetails(builder, order);

            builder.append("<p>You have successfully cancelled the the above order. A refund will be issued.</p>");

            String htmlBody = builder.toString();

            //Sends the email
            sendEmail(userEntity.getEmail(), htmlBody);
        }
        try {
            issueRefund(orderEntity.getChargeId(), "requested_by_customer");
        } catch (StripeException e) {
            log.error("Stripe Error:" + e.getMessage());
        }
    }

    private StringBuilder buildOrderDetails(StringBuilder builder, Order order) {
        for (OrderFood orderFood : order.getFood()) {
            builder.append("<p>").append(orderFood.getRestaurantName()).append(": ");
            List<OrderItems> itemsList = orderFood.getItems();
            for (int i = 0; i < itemsList.size(); i++) {
                if (i < itemsList.size() - 1)
                    builder.append(itemsList.get(i).getName()).append(", ");
                else
                    builder.append(itemsList.get(i).getName());
            }
            builder.append("</p>");
        }
        BigDecimal price = order.getPrice().getFood().divide(new BigDecimal("100"), 2, RoundingMode.UNNECESSARY);
        builder.append("<b>Total: </b> $").append(price);
        return builder;
    }

    private void sendEmail(String emailTo, String emailBody) {
        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(emailTo))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withHtml(new Content().withCharset("UTF-8").withData(emailBody))).withSubject(new Content()
                                .withCharset("UTF-8").withData("Scrumptious Order Confirmation"))).withSource(emailFrom);
        emailService.sendEmail(request);
        log.info("sendOrderConfirmation: Order confirmation email sent!");
    }

    /*
    Section for conversion methods (DTOs and Entities)
     */

    public ResponseEntity<Void> patchDriverOrders(Long order, Long driver, Boolean assign) {
        OrderEntity orderEntity = orderRepo.findById(order).get();
        DriverEntity driverEntity = driverRepo.findById(driver).get();
        orderEntity.setDriver(driverEntity);
        orderRepo.save(orderEntity);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public Order convertToDTO(OrderEntity orderEntity) {
        Order orderDTO = modelMapper.map(orderEntity, Order.class);

        orderDTO.setOrderTime(convertTimeToDTO(orderEntity.getOrderTimeEntity()));

        orderDTO.setOrderType((orderEntity.getDelivery() ? Order.OrderTypeEnum.DELIVERY : Order.OrderTypeEnum.PICKUP));
        if (orderEntity.getDriver() != null) orderDTO.setDriverId(String.valueOf(orderEntity.getDriver().getId()));
        List<FoodOrderEntity> foodOrderEntities = orderEntity.getItems();
        List<OrderFood> orderFoodList = new ArrayList<>();

        for (FoodOrderEntity foodOrderEntity : foodOrderEntities) {

            OrderFood orderFood = new OrderFood();
            orderFood.setRestaurantId(String.valueOf(foodOrderEntity.getRestaurant().getId()));
            orderFood.setRestaurantName(foodOrderEntity.getRestaurant().getName());

            if (!orderFoodList.contains(orderFood)) {
                List<OrderItems> newOrderItems = new ArrayList<>();
                newOrderItems.add(convertItemToDTO(foodOrderEntity));
                orderFood.setItems(newOrderItems);
                orderFoodList.add(orderFood);
            } else {
                orderFoodList.get(orderFoodList.indexOf(orderFood)).getItems().add(convertItemToDTO(foodOrderEntity));
            }
        }

        OrderPrice orderPrice = modelMapper.map(orderEntity.getPriceEntity(), OrderPrice.class);
        orderDTO.setPrice(orderPrice);

        orderDTO.setFood(orderFoodList);

        return orderDTO;

    }

    public OrderItems convertItemToDTO(FoodOrderEntity foodOrderEntity) {
        MenuItemEntity menuItemEntity = foodOrderEntity.getMenuItem();
        String[] configurations = foodOrderEntity.getConfigurations().get(0).getConfigurationName().split(" ");
        /*String itemId = configurations[0];*/
        String quantity = configurations[configurations.length - 1];


        OrderItems orderItems = modelMapper.map(menuItemEntity, OrderItems.class);
        orderItems.setId(String.valueOf(menuItemEntity.getId()));
        orderItems.setQuantity(Integer.parseInt(quantity));
        orderItems.setConfigurations(Collections.singletonList(quantity));
        return orderItems;
    }

    public OrderOrderTime convertTimeToDTO(OrderTimeEntity orderTimeEntity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT);
        OrderOrderTime orderTimeDTO = new OrderOrderTime();
        if (orderTimeEntity.getPlaced() != null) {
            String orderPlaced = DateTimeFormatter.ofPattern(TIME_FORMAT).format(
                    orderTimeEntity.getPlaced());
            orderTimeDTO.setOrderPlaced(orderPlaced);
        }
        if (orderTimeEntity.getOrderComplete() != null) {
            String orderComplete = formatter.format(
                    orderTimeEntity.getOrderComplete());
            orderTimeDTO.setDelivered(orderComplete);
        }
        if (orderTimeEntity.getDeliverySlot() != null)
            orderTimeDTO.setDeliverySlot(formatter.format(orderTimeEntity.getDeliverySlot()));
        if (orderTimeEntity.getRestaurantAccept() != null)
            orderTimeDTO.setRestaurantAccept(formatter.format(orderTimeEntity.getRestaurantAccept()));
        if (orderTimeEntity.getDriverPickUp() != null)
            orderTimeDTO.setDriverPickUp(formatter.format(orderTimeEntity.getDriverPickUp()));
        if (orderTimeEntity.getDriverAccept() != null)
            orderTimeDTO.setDriverAccept(formatter.format(orderTimeEntity.getDriverAccept()));
        if (orderTimeEntity.getDriverComplete() != null)
            orderTimeDTO.setDelivered(formatter.format(orderTimeEntity.getDriverComplete()));
        if (orderTimeEntity.getRestaurantComplete() != null)
            orderTimeDTO.setRestaurantComplete(formatter.format(orderTimeEntity.getRestaurantComplete()));
        return orderTimeDTO;
    }

    public OrderEntity convertToEntity(Order orderDTO) {
        OrderEntity orderEntity = modelMapper.map(orderDTO, OrderEntity.class);

        if (orderDTO.getDriverId() != null) {
            Optional<DriverEntity> driverEntity = driverRepo.findById(Long.parseLong(orderDTO.getDriverId()));
            driverEntity.ifPresent(orderEntity::setDriver);
        }

        orderEntity.setDelivery(orderDTO.getOrderType().equals(Order.OrderTypeEnum.DELIVERY));

        List<OrderFood> orderFoodList = orderDTO.getFood();
        List<FoodOrderEntity> foodOrderEntities = new ArrayList<>();

        if (orderFoodList != null && !orderFoodList.isEmpty()) {
            for (OrderFood orderFood : orderFoodList) {
                for (OrderItems orderItemDTO : orderFood.getItems()) {
                    if (menuItemRepo.findById(Long.parseLong(orderItemDTO.getId())).isPresent()) {
                        List<OrderConfigurationEntity> configurationEntities = new ArrayList<>();
                        FoodOrderEntity foodOrderEntity = new FoodOrderEntity();
                        foodOrderEntity.setRestaurant(restaurantRepo.findById(Long.parseLong(orderFood.getRestaurantId())).orElse(null));
                        foodOrderEntity.setMenuItem(menuItemRepo.findById(Long.parseLong(orderItemDTO.getId())).get());
                        OrderConfigurationEntity configurationEntity = new OrderConfigurationEntity()
                                .setConfigurationName(orderItemDTO.getId() + " Quantity: " + orderItemDTO.getConfigurations().get(0));
                        configurationEntities.add(configurationEntity);
                        foodOrderEntity.setConfigurations(configurationEntities);
                        foodOrderEntities.add(foodOrderEntity);
                    }
                }
            }
            orderEntity.setItems(foodOrderEntities);
            PriceEntity priceEntity = modelMapper.map(orderDTO.getPrice(), PriceEntity.class);
            orderEntity.setPriceEntity(priceEntity);
        }

        orderEntity.setOrderTimeEntity(convertTimeDTOToEntity(orderDTO.getOrderTime()));
        return orderEntity;
    }

    private OrderTimeEntity convertTimeDTOToEntity(OrderOrderTime orderTimeDTO) {
        OrderTimeEntity orderTimeEntity = new OrderTimeEntity();
        if (orderTimeDTO.getOrderPlaced() != null)
            orderTimeEntity.setPlaced(ZonedDateTime.parse(orderTimeDTO.getOrderPlaced()));
        if (orderTimeDTO.getDriverAccept() != null)
            orderTimeEntity.setDriverAccept(ZonedDateTime.parse(orderTimeDTO.getDriverAccept()));
        if (orderTimeDTO.getDeliverySlot() != null)
            orderTimeEntity.setDeliverySlot(ZonedDateTime.parse(orderTimeDTO.getDeliverySlot()));
        if (orderTimeDTO.getDriverPickUp() != null)
            orderTimeEntity.setDriverPickUp(ZonedDateTime.parse(orderTimeDTO.getDriverPickUp()));
        if (orderTimeDTO.getRestaurantComplete() != null)
            orderTimeEntity.setRestaurantComplete(ZonedDateTime.parse(orderTimeDTO.getOrderPlaced()));
        if (orderTimeDTO.getRestaurantAccept() != null)
            orderTimeEntity.setRestaurantAccept(ZonedDateTime.parse(orderTimeDTO.getRestaurantAccept()));
        if (orderTimeDTO.getDelivered() != null)
            orderTimeEntity.setOrderComplete(ZonedDateTime.parse(orderTimeDTO.getDelivered()));
        return orderTimeEntity;
    }


    /*
    Section for stripe operations
     */

    public ChargeResponse createStripeCharge(ChargeRequest chargeRequest) {
        Stripe.apiKey = stripeKey;

        ChargeCreateParams params = ChargeCreateParams.builder()
                .setAmount(chargeRequest.getChargePrice())
                .setCurrency("usd")
                .setDescription("Test Charge")
                .setSource(chargeRequest.getTokenId())
                .build();

        Charge charge;
        ChargeResponse chargeResponse;
        try {
            charge = Charge.create(params);
            chargeResponse = new ChargeResponse(charge.toJson(), "");
        } catch (StripeException e) {
            log.error("Stripe Error:" + e.getMessage());
            chargeResponse = new ChargeResponse(null, e.getMessage().split(";")[0]);
        }

        log.info("createStripeCharge: Stripe charge successfully created.");
        return chargeResponse;
    }

    private void issueRefund(String chargeId, String reason) throws StripeException {
        Stripe.apiKey = stripeKey;

        Map<String, Object> params = new HashMap<>();
        params.put("charge", chargeId);
        params.put("reason", reason);

        try {
            Refund refund = Refund.create(params);
        } catch (StripeException e) {
            log.error("Stripe Error:" + e.getMessage());
        }

        log.info("Refund issued for: " + chargeId);

    }

}