package com.smoothstack.order.service;

import com.database.ormlibrary.food.MenuItemEntity;
import com.database.ormlibrary.order.FoodOrderEntity;
import com.database.ormlibrary.order.OrderEntity;
import com.database.ormlibrary.order.OrderTimeEntity;
import com.database.ormlibrary.order.PriceEntity;
import com.smoothstack.order.model.OrderFood;
import com.smoothstack.order.model.OrderItems;
import com.smoothstack.order.repo.*;
import com.smoothstack.order.api.OrderApi;
import com.smoothstack.order.model.CreateResponse;
import com.smoothstack.order.model.Order;
import error.OrderTimeException;
import io.swagger.annotations.ApiParam;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<?> createOrder(@ApiParam(value = "")
                                                      @Valid @RequestBody(required = false) OrderEntity orderEntity) {
        //OrderEntity orderEntity = convertToEntity(orderDTO);
        orderEntity.setActive(true);
        ZonedDateTime deliverySlot = orderEntity.getOrderTimeEntity().getDeliverySlot();
        ZonedDateTime restaurantAccept = orderEntity.getOrderTimeEntity().getRestaurantAccept();
        long diff = ChronoUnit.MINUTES.between(restaurantAccept, deliverySlot);
        if (diff < 15) return ResponseEntity.badRequest().body(
                new OrderTimeException("Time slot too early"));

        float totalFoodPrice = 0f;

        for (FoodOrderEntity foodOrder : orderEntity.getItems())
            for (MenuItemEntity menuItemEntity : foodOrder.getOrderItems())
                totalFoodPrice += menuItemEntity.getPrice();

        orderEntity.getPriceEntity().setFoodPrice(totalFoodPrice);
        return ResponseEntity.ok(orderRepo.save(orderEntity));
        /*return ResponseEntity.ok(new CreateResponse().type(CreateResponse.TypeEnum.STRIPE)
                .id(String.valueOf(orderEntity.getId())));*/
    }

    @Override
    public ResponseEntity<List<OrderEntity>> getOrder(@ApiParam(value = "if true only returns pending orders") @Valid @RequestParam(value = "active", required = false) Boolean active){
        return ResponseEntity.ok((List<OrderEntity>)orderRepo.findAll());
    }

    public ResponseEntity<?> createSampleOrder (){
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
        List <FoodOrderEntity> foodOrderEntities = new ArrayList<>();
        foodOrderEntities.add(foodOrderEntity);

        PriceEntity priceEntity = new PriceEntity().setFoodPrice(23.09f);

        OrderEntity orderEntity = new OrderEntity()
                .setId(23L).setDelivery(true).setRefunded(false)
                .setAddress("123 Street St").setOrderTimeEntity(orderTimeEntity)
                .setItems(foodOrderEntities).setPriceEntity(priceEntity);

        return ResponseEntity.ok (orderRepo.save(orderEntity));
    }

    private OrderEntity convertToEntity(Order orderDTO) {
        OrderEntity orderEntity = modelMapper.map(orderDTO, OrderEntity.class);

        if (orderDTO.getDriverId() != null) {
            if (driverRepo.findById(Long.parseLong(orderDTO.getDriverId())).isPresent())
                orderEntity.setDriver(driverRepo.findById(Long.parseLong(orderDTO.getDriverId())).get());
        }

        orderEntity.setDelivery(orderDTO.getOrderType().equals(Order.OrderTypeEnum.DELIVERY));

        List<OrderFood> orderFoodList = orderDTO.getFood();
        List<FoodOrderEntity> foodOrderEntities = new ArrayList<>();

        if (orderFoodList != null && orderFoodList.size() > 0) {
            //finds order lists from all restaurants
            for (OrderFood orderFood : orderFoodList){
                List<MenuItemEntity> itemEntities = new ArrayList<>();
                //populates a specific order list with items
                for (OrderItems orderItemDTO: orderFood.getItems()){

                    //testing purposes only to add menu items

                    if (!menuItemRepo.findById(Long.parseLong(orderItemDTO.getId())).isPresent())
                        menuItemRepo.save(new MenuItemEntity().setId(
                                Long.parseLong(orderItemDTO.getId())).setName(orderItemDTO.getName()));

                    //actual code

                    if (menuItemRepo.findById(Long.parseLong(orderItemDTO.getId())).isPresent()){
                        itemEntities.add(menuItemRepo.findById(Long.parseLong(orderItemDTO.getId())).get());
                    }
                }
                FoodOrderEntity foodOrderEntity = new FoodOrderEntity();
                //foodOrderEntity.setItem(itemEntities);
                foodOrderEntities.add(foodOrderEntity);
            }
            orderEntity.setItems(foodOrderEntities);
            orderEntity.setOrderTimeEntity(new OrderTimeEntity().setDeliverySlot(
                    ZonedDateTime.parse(orderDTO.getOrderTime().getDeliverySlot()))
            .setRestaurantAccept(ZonedDateTime.parse(orderDTO.getOrderTime().getRestaurantAccept())));

        }
        return orderEntity;
    }

}
