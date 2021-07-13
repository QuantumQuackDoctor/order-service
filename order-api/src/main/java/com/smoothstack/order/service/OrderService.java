package com.smoothstack.order.service;

import com.database.ormlibrary.food.MenuItemEntity;
import com.database.ormlibrary.order.FoodOrderEntity;
import com.database.ormlibrary.order.OrderEntity;
import com.database.ormlibrary.order.OrderTimeEntity;
import com.smoothstack.order.model.OrderFood;
import com.smoothstack.order.model.OrderItems;
import com.smoothstack.order.repo.DriverRepo;
import com.smoothstack.order.repo.MenuItemRepo;
import com.smoothstack.order.repo.OrderRepo;
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
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeFormatter;
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
    private final ModelMapper modelMapper;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy - HH:mm:ss Z");

    public OrderService(OrderRepo orderRepo, DriverRepo driverRepo, MenuItemRepo menuItemRepo) {
        this.orderRepo = orderRepo;
        this.driverRepo = driverRepo;
        this.menuItemRepo = menuItemRepo;
        this.modelMapper = new ModelMapper();
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return OrderApi.super.getRequest();
    }

    @Override
    public ResponseEntity<?> createOrder(@ApiParam(value = "")
                                                      @Valid @RequestBody(required = false) Order orderDTO) {
        OrderEntity orderEntity = convertToEntity(orderDTO);
        orderEntity.setActive(true);
        ZonedDateTime deliverySlot = orderEntity.getOrderTimeEntity().getDeliverySlot();
        ZonedDateTime restaurantAccept = orderEntity.getOrderTimeEntity().getRestaurantAccept();
        long diff = Math.abs(ChronoUnit.MINUTES.between(restaurantAccept, deliverySlot));
        if (diff < 15) return ResponseEntity.badRequest().body(
                new OrderTimeException("Time slot too early"));

        orderRepo.save(orderEntity);
        return ResponseEntity.ok(new CreateResponse().type(CreateResponse.TypeEnum.STRIPE)
                .id(String.valueOf(orderEntity.getId())));

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
                foodOrderEntity.setItems(itemEntities);
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
