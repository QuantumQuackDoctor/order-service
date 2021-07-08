package com.smoothstack.order.service;

import com.database.ormlibrary.food.MenuItemEntity;
import com.database.ormlibrary.order.FoodOrderEntity;
import com.database.ormlibrary.order.OrderEntity;
import com.smoothstack.order.model.OrderFood;
import com.smoothstack.order.model.OrderItems;
import com.smoothstack.order.repo.DriverRepo;
import com.smoothstack.order.repo.MenuItemRepo;
import com.smoothstack.order.repo.OrderRepo;
import com.smoothstack.order.api.OrderApi;
import com.smoothstack.order.model.CreateResponse;
import com.smoothstack.order.model.Order;
import io.swagger.annotations.ApiParam;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
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
    public ResponseEntity<CreateResponse> createOrder(@ApiParam(value = "")
                                                      @Valid @RequestBody(required = false) Order order) {

        OrderEntity entity = convertToEntity(order);
        entity.setActive(true);
        return ResponseEntity.ok(new CreateResponse().type(CreateResponse.TypeEnum.STRIPE)
                .id(String.valueOf(entity.getId())));

    }

    private OrderEntity convertToEntity(Order order) {
        OrderEntity orderEntity = modelMapper.map(order, OrderEntity.class);

        if (order.getDriverId() != null) {
            if (driverRepo.findById(Long.parseLong(order.getDriverId())).isPresent())
                orderEntity.setDriver(driverRepo.findById(Long.parseLong(order.getDriverId())).get());
        }

        orderEntity.setDelivery(order.getOrderType().equals(Order.OrderTypeEnum.DELIVERY));

        List<OrderFood> food = order.getFood();
        List<FoodOrderEntity> foodOrderEntities = new ArrayList<>();

        if (food != null && food.size() > 0) {
            for (OrderFood orderFood : food){
                List<MenuItemEntity> itemEntities = new ArrayList<>();
                for (OrderItems orderItem: orderFood.getItems()){
                    if (menuItemRepo.findById(Long.parseLong(orderItem.getId())).isPresent()){
                        itemEntities.add(menuItemRepo.findById(Long.parseLong(orderItem.getId())).get());
                    }
                }
                FoodOrderEntity foodOrderEntity = new FoodOrderEntity();
                //foodOrderEntity.setId(entity.getId());
                foodOrderEntity.setItems(itemEntities);
                foodOrderEntities.add(foodOrderEntity);
            }
            orderEntity.setItems(foodOrderEntities);
        }
        return orderEntity;
    }
}
