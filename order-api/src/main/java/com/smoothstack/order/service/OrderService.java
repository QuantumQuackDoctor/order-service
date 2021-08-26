package com.smoothstack.order.service;

import com.database.ormlibrary.food.MenuItemEntity;
import com.database.ormlibrary.order.FoodOrderEntity;
import com.database.ormlibrary.order.OrderEntity;
import com.database.ormlibrary.order.OrderTimeEntity;
import com.database.ormlibrary.order.PriceEntity;
import com.smoothstack.order.exception.OrderExceptionHandler;
import com.smoothstack.order.exception.ValueNotPresentException;
import com.smoothstack.order.model.*;
import com.smoothstack.order.repo.*;
import com.smoothstack.order.api.OrderApi;
import com.smoothstack.order.exception.OrderTimeException;
import io.swagger.annotations.ApiParam;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService implements OrderApi {

    @Autowired
    private final OrderRepo orderRepo;
    @Autowired
    private final DriverRepo driverRepo;
    @Autowired
    private final MenuItemRepo menuItemRepo;
    @Autowired
    private final FoodOrderRepo foodOrderRepo;

    private final ModelMapper modelMapper;

    public OrderService(OrderRepo orderRepo, DriverRepo driverRepo, MenuItemRepo menuItemRepo, FoodOrderRepo foodOrderRepo) {
        this.orderRepo = orderRepo;
        this.driverRepo = driverRepo;
        this.menuItemRepo = menuItemRepo;
        this.foodOrderRepo = foodOrderRepo;
        this.modelMapper = new ModelMapper();
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return OrderApi.super.getRequest();
    }

    @Override
    public ResponseEntity<Void> deleteOrder(String idString) throws ValueNotPresentException {
        Long id = Long.parseLong(idString);
        OrderEntity orderEntity = orderRepo.findById(id).isPresent() ?
                orderRepo.findById(id).get() : null;
        List<FoodOrderEntity> foodOrderEntityList;
        if (orderEntity == null) new ResponseEntity<>(HttpStatus.OK);
        try {
            foodOrderEntityList = orderEntity.getItems();
        } catch (Exception e) {
            throw new ValueNotPresentException("no value present");
        }

//        for (FoodOrderEntity foodOrderEntity : foodOrderEntityList)
//            foodOrderRepo.delete(foodOrderEntity);
        orderRepo.deleteById(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<CreateResponse> createOrder(@ApiParam(value = "")
                                          @RequestBody(required = false) Order orderDTO) throws OrderTimeException {
        OrderEntity orderEntity = convertToEntity(orderDTO);
        orderEntity.setActive(true);
        if (orderEntity.getDelivery()) {
            ZonedDateTime deliverySlot = orderEntity.getOrderTimeEntity().getDeliverySlot();
            ZonedDateTime restaurantAccept = orderEntity.getOrderTimeEntity().getRestaurantAccept();
            long diff = ChronoUnit.MINUTES.between(restaurantAccept, deliverySlot);
            if (diff < 15) throw new OrderTimeException("Time slot too early");
        }

        orderRepo.save(orderEntity);
        return ResponseEntity.ok(new CreateResponse().type(CreateResponse.TypeEnum.STRIPE)
                .id(String.valueOf(orderEntity.getId())).setAddress(orderEntity.getAddress()));
    }

    @Override
    public ResponseEntity<Order> getOrder(@ApiParam(value = "if true only returns pending orders") @Valid @RequestParam(value = "id", required = false) String id) throws ValueNotPresentException {

        Optional<OrderEntity> orderEntity = orderRepo.findById(Long.parseLong(id));
        Order orderDTO = new Order();

        try {
            orderDTO = convertToDTO(orderEntity.get());
        } catch (Exception e) {
            throw new ValueNotPresentException("No item of that id in the database");
        }


        return ResponseEntity.ok(orderDTO);
    }



    @Override
    public ResponseEntity<List<Order>> getActiveOrders(String sortType, Integer page, Integer size) {
        Iterable<OrderEntity> orderEntities = orderRepo.findAll();
        List<Order> orderDTOs = new ArrayList<>();
        for (OrderEntity orderEntity : orderEntities) {
             if (orderEntity.getRefunded() == null || orderEntity.getRefunded() == false )
                orderDTOs.add(convertToDTO(orderEntity));
        }

        orderDTOs = sortList(orderDTOs, sortType);
        orderDTOs = pageList(orderDTOs, page, size);

        return ResponseEntity.ok(orderDTOs);
    }

    public List<Order> pageList(List<Order> orders, Integer page, Integer size) {
        if((page + 1) * size > orders.size()) {
            return orders.subList(page * size, orders.size());
        }
        return orders.subList(page * size, (page + 1) * size);
    }

    public List<Order> sortList(List<Order> orders, String sortType) {
        switch (sortType != null? sortType : "") {
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

    public OrderEntity createSampleOrder() {
        OrderTimeEntity orderTimeEntity = new OrderTimeEntity().setDeliverySlot(ZonedDateTime.parse("2011-12-03T10:15:30+01:00"))
                .setRestaurantAccept(ZonedDateTime.parse("2011-12-03T10:35:30+01:00"));

        List<MenuItemEntity> orderItemsEntities = new ArrayList<>();
        MenuItemEntity menuItemEntity1 = new MenuItemEntity().setName("Sample Item 1");
        MenuItemEntity menuItemEntity2 = new MenuItemEntity().setName("Sample Item 2");
        orderItemsEntities.add(menuItemEntity1);
        orderItemsEntities.add(menuItemEntity2);
        menuItemRepo.save(menuItemEntity1);
        menuItemRepo.save(menuItemEntity2);

        FoodOrderEntity foodOrderEntity = new FoodOrderEntity().setId(1L).setOrderItems(orderItemsEntities).setRestaurantId(1L);
        foodOrderRepo.save(foodOrderEntity);
        List<FoodOrderEntity> foodOrderEntities = new ArrayList<>();
        foodOrderEntities.add(foodOrderEntity);

        PriceEntity priceEntity = new PriceEntity().setFood(23.09f);

        OrderEntity orderEntity = new OrderEntity()
                .setId(23L).setDelivery(true).setRefunded(false)
                .setAddress("123 Street St").setOrderTimeEntity(orderTimeEntity)
                .setItems(foodOrderEntities).setPriceEntity(priceEntity);

        return orderRepo.save(orderEntity);
    }

    @Override
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
        List<OrderItems> orderItemsList = new ArrayList<>();

        for (FoodOrderEntity foodOrderEntity : foodOrderEntities) {
            OrderFood orderFood = modelMapper.map(foodOrderEntity, OrderFood.class);
            orderFood.setRestaurantId(String.valueOf(foodOrderEntity.getRestaurantId()));
            orderFoodList.add(orderFood);
            for (MenuItemEntity menuItemEntity : foodOrderEntity.getOrderItems()) {
                OrderItems orderItems = modelMapper.map(menuItemEntity, OrderItems.class);
                orderItems.setId(String.valueOf(menuItemEntity.getId()));
                orderItemsList.add(orderItems);
            }
            orderFood.setItems(orderItemsList);
        }

        OrderPrice orderPrice = modelMapper.map (orderEntity.getPriceEntity(), OrderPrice.class);
        orderDTO.setPrice(orderPrice);

        orderDTO.setFood(orderFoodList);

        return orderDTO;

    }

    public OrderOrderTime convertTimeToDTO (OrderTimeEntity orderTimeEntity){

        String deliveryTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(
                orderTimeEntity.getDeliverySlot());
        String restaurantAccept = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(
                orderTimeEntity.getRestaurantAccept());
        return new OrderOrderTime().deliverySlot(deliveryTime).restaurantAccept(restaurantAccept);

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
                foodOrderEntity.setRestaurantId(Long.parseLong (orderFood.getRestaurantId()));
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

    public void insertSampleMenuItems(){
        MenuItemEntity menuItemEntity1 = new MenuItemEntity().setName("Sample Item 1").setId(1L).setPrice(5.3f);
        MenuItemEntity menuItemEntity2 = new MenuItemEntity().setName("Sample Item 2").setId(2L).setPrice(3.5f);
        if (!menuItemRepo.findById(1L).isPresent())
            menuItemRepo.save(menuItemEntity1);
        if (!menuItemRepo.findById(2L).isPresent())
            menuItemRepo.save(menuItemEntity2);
    }

}
