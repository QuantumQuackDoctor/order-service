package com.smoothstack.order.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.database.ormlibrary.food.MenuItemEntity;
import com.database.ormlibrary.order.FoodOrderEntity;
import com.database.ormlibrary.order.OrderEntity;
import com.database.ormlibrary.order.OrderTimeEntity;
import com.database.ormlibrary.order.PriceEntity;
import com.database.ormlibrary.user.UserEntity;
import com.smoothstack.order.exception.OrderTimeException;
import com.smoothstack.order.exception.UserNotFoundException;
import com.smoothstack.order.exception.ValueNotPresentException;
import com.smoothstack.order.model.*;
import com.smoothstack.order.repo.*;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.param.ChargeCreateParams;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
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

    private final ModelMapper modelMapper;

    public OrderService(OrderRepo orderRepo, DriverRepo driverRepo, MenuItemRepo menuItemRepo, FoodOrderRepo foodOrderRepo, RestaurantRepo restaurantRepo, UserRepo userRepo, AmazonSimpleEmailService emailService) {
        this.orderRepo = orderRepo;
        this.driverRepo = driverRepo;
        this.menuItemRepo = menuItemRepo;
        this.foodOrderRepo = foodOrderRepo;
        this.restaurantRepo = restaurantRepo;
        this.userRepo = userRepo;
        this.emailService = emailService;
        this.modelMapper = new ModelMapper();
    }

    public ResponseEntity<Void> deleteOrder(Long id) throws ValueNotPresentException {
        OrderEntity orderEntity = orderRepo.findById(id).isPresent() ?
                orderRepo.findById(id).get() : null;
        if (orderEntity == null) return new ResponseEntity<>(HttpStatus.OK);
        List<FoodOrderEntity> foodOrderEntityList = orderEntity.getItems();
        for (FoodOrderEntity foodOrderEntity : foodOrderEntityList)
            foodOrderRepo.delete(foodOrderEntity);
        orderRepo.deleteById(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<CreateResponse> createOrder(Order orderDTO,
                                                      Long userId) throws OrderTimeException, UserNotFoundException {
        OrderEntity orderEntity = convertToEntity(orderDTO);
        orderEntity.setActive(true);
        if (orderEntity.getDelivery()) {
            ZonedDateTime deliverySlot = orderEntity.getOrderTimeEntity().getDeliverySlot();
            ZonedDateTime restaurantAccept = orderEntity.getOrderTimeEntity().getRestaurantAccept();
            long diff = ChronoUnit.MINUTES.between(restaurantAccept, deliverySlot);
            if (diff < 15) throw new OrderTimeException("Time slot too early");
        }


        Optional<UserEntity> userEntityOptional = userRepo.findById(userId);
        if (userEntityOptional.isPresent()) {
            userEntityOptional.get().getOrderList().add(orderEntity);
        } else {
            throw new UserNotFoundException("User not found.");
        }
        orderRepo.save(orderEntity);
        return ResponseEntity.ok(new CreateResponse().type(CreateResponse.TypeEnum.STRIPE)
                .id(String.valueOf(orderEntity.getId())).setAddress(orderEntity.getAddress()));
    }

    public List<Order> getUserOrders(Long userId) throws UserNotFoundException {
        Optional<UserEntity> userEntityOptional = userRepo.findById(userId);
        if (userEntityOptional.isPresent()) {
            List<Order> orderList = new ArrayList<>();
            userEntityOptional.get().getOrderList().forEach(orderEntity -> orderList.add(convertToDTO(orderEntity)));
            return orderList;
        }
        throw new UserNotFoundException("User not found!");
    }

    public ResponseEntity<Order> getOrder(Long id) throws ValueNotPresentException {

        Optional<OrderEntity> orderEntity = orderRepo.findById(id);
        Order orderDTO = new Order();

        try {
            orderDTO = convertToDTO(orderEntity.get());
        } catch (Exception e) {
            throw new ValueNotPresentException("No item of that id in the database");
        }


        return ResponseEntity.ok(orderDTO);
    }


    public ResponseEntity<List<Order>> getActiveOrders(String sortType, Integer page, Integer size) {
        Iterable<OrderEntity> orderEntities = orderRepo.findAll();
        List<Order> orderDTOs = new ArrayList<>();
        for (OrderEntity orderEntity : orderEntities) {
            if (orderEntity.getRefunded() == null || !orderEntity.getRefunded())
                orderDTOs.add(convertToDTO(orderEntity));
        }

        orderDTOs = sortList(orderDTOs, sortType);
        orderDTOs = pageList(orderDTOs, page, size);

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

    public ResponseEntity<Void> patchOrders(Order order) {
        OrderEntity orderEntity = convertToEntity(order);
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
        List<OrderItems> orderItemsList;

        for (FoodOrderEntity foodOrderEntity : foodOrderEntities) {
            orderItemsList = new ArrayList<>();
            OrderFood orderFood = modelMapper.map(foodOrderEntity, OrderFood.class);
            orderFood.setRestaurantId(String.valueOf(foodOrderEntity.getRestaurantId()));
            orderFood.setRestaurantName(restaurantRepo.findById(foodOrderEntity.getRestaurantId()).get().getName());
            orderFoodList.add(orderFood);
            for (MenuItemEntity menuItemEntity : foodOrderEntity.getOrderItems()) {
                OrderItems orderItems = modelMapper.map(menuItemEntity, OrderItems.class);
                orderItems.setId(String.valueOf(menuItemEntity.getId()));
                orderItemsList.add(orderItems);
            }
            orderFood.setItems(orderItemsList);
        }

        OrderPrice orderPrice = modelMapper.map(orderEntity.getPriceEntity(), OrderPrice.class);
        orderDTO.setPrice(orderPrice);

        orderDTO.setFood(orderFoodList);

        return orderDTO;

    }

    public OrderOrderTime convertTimeToDTO(OrderTimeEntity orderTimeEntity) {

        OrderOrderTime orderTimeDTO = new OrderOrderTime();
        if (orderTimeEntity.getOrderComplete() != null) {
            String orderComplete = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(
                    orderTimeEntity.getOrderComplete());
            orderTimeDTO.setDelivered(orderComplete);
        }
        orderTimeDTO.setDeliverySlot(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(
                orderTimeEntity.getDeliverySlot()));
        orderTimeDTO.setRestaurantAccept(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(
                orderTimeEntity.getRestaurantAccept()));

        return orderTimeDTO;
    }

    public OrderEntity convertToEntity(Order orderDTO) {
        OrderEntity orderEntity = modelMapper.map(orderDTO, OrderEntity.class);

        if (orderDTO.getDriverId() != null) {
            if (driverRepo.findById(Long.parseLong(orderDTO.getDriverId())).isPresent())
                orderEntity.setDriver(driverRepo.findById(Long.parseLong(orderDTO.getDriverId())).get());
        }

        orderEntity.setDelivery(orderDTO.getOrderType().equals(Order.OrderTypeEnum.DELIVERY));

       /* OrderOrderTime orderTimeDTO =
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd'T'HH:mm:ss.SSSX");*/

        List<OrderFood> orderFoodList = orderDTO.getFood();
        List<FoodOrderEntity> foodOrderEntities = new ArrayList<>();

        if (orderFoodList != null && orderFoodList.size() > 0) {
            //finds order lists from all restaurants

            for (OrderFood orderFood : orderFoodList) {
                List<MenuItemEntity> itemEntities = new ArrayList<>();
                //populates a specific order list with items

                for (OrderItems orderItemDTO : orderFood.getItems()) {

                    if (menuItemRepo.findById(Long.parseLong(orderItemDTO.getId())).isPresent()) {
                        itemEntities.add(menuItemRepo.findById(Long.parseLong(orderItemDTO.getId())).get());
                    }

                }
                FoodOrderEntity foodOrderEntity = new FoodOrderEntity();
                foodOrderEntity.setOrderItems(itemEntities);
                foodOrderEntity.setRestaurantId(Long.parseLong(orderFood.getRestaurantId()));
                foodOrderEntities.add(foodOrderEntity);
            }
            orderEntity.setItems(foodOrderEntities);
            orderEntity.setOrderTimeEntity(new OrderTimeEntity().setDeliverySlot(
                            ZonedDateTime.parse(orderDTO.getOrderTime().getDeliverySlot()))
                    .setRestaurantAccept(ZonedDateTime.parse(orderDTO.getOrderTime().getRestaurantAccept())));

            PriceEntity priceEntity = modelMapper.map(orderDTO.getPrice(), PriceEntity.class);
            orderEntity.setPriceEntity(priceEntity);

        }
        return orderEntity;
    }

    public ChargeResponse createStripeCharge(ChargeRequest chargeRequest) {
        //TODO store stripe sk somewhere else
        Stripe.apiKey = "sk_test_51JUCrpATHQZZA29uttyLiHs6lJWkVcybe4Tu3a4sfJTnxYLAMwAr1hdfMvtXSGAFfcXKnaUM6SG2kL42IDXDfz1c00SN07hSbc";

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
            chargeResponse = new ChargeResponse(null, e.getMessage().split(";")[0]);
        }

        return chargeResponse;
    }

    public Order sendOrderConfirmation(Long orderId, Long userId) {
        StringBuilder builder = new StringBuilder();
        UserEntity entity = userRepo.findById(userId).get();
        if (entity.getSettings().getNotifications().getEmail()) {
            String emailTo = entity.getEmail();
            Order order = convertToDTO(orderRepo.findById(orderId).orElse(null));
            builder.append("<h1>Scrumptious Order Confirmation: <h1> \n <strong>Order Items: </strong>");
            for (OrderFood orderFood : order.getFood()) {
                builder.append("<p>").append(orderFood.getRestaurantName()).append(": ");
                List<OrderItems> itemsList = orderFood.getItems();
                for (int i = 0; i < itemsList.size(); i++) {
                    if (i < itemsList.size() - 1)
                        builder.append(itemsList.get(i).getName()).append(", ");
                    else
                        builder.append(itemsList.get(i).getName());
                }
                builder.append("<p>");
            }
            BigDecimal price = order.getPrice().getFood().divide(new BigDecimal("100"));
            builder.append("<b>Total: </b> $").append(price);
            String htmlBody = builder.toString();

            SendEmailRequest request = new SendEmailRequest()
                    .withDestination(new Destination().withToAddresses(emailTo))
                    .withMessage(new Message()
                            .withBody(new Body()
                                    .withHtml(new Content().withCharset("UTF-8").withData(htmlBody))).withSubject(new Content()
                                    .withCharset("UTF-8").withData("Scrumptious Order Confirmation"))).withSource(emailFrom);
            emailService.sendEmail(request);
            return order;
        }return null;
    }
}
