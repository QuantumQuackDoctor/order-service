package com.smoothstack.order.service;

import com.database.ormlibrary.food.MenuItemEntity;
import com.database.ormlibrary.order.FoodOrderEntity;
import com.database.ormlibrary.order.OrderEntity;
import com.database.ormlibrary.order.OrderTimeEntity;
import com.database.ormlibrary.order.PriceEntity;
import com.smoothstack.order.Main;
import com.smoothstack.order.model.*;
import com.smoothstack.order.repo.*;
import error.OrderTimeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest (classes = { Main.class })
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Test
    void createOrderTest(){
        //inserts sample items in empty db

        OrderEntity orderEntity = getSampleOrder();

        orderService.insertSampleMenuItems();

        Order orderDTO = orderService.convertToDTO(orderEntity);

        CreateResponse insertedResponse = (CreateResponse) orderService.createOrder(orderDTO).getBody();

        assertEquals (orderDTO.getAddress(), insertedResponse.getAddress());

        OrderOrderTime newDeliveryTime = orderDTO.getOrderTime();

        newDeliveryTime.setDeliverySlot("2011-12-03T10:25:30+01:00");

        orderDTO.setOrderTime(newDeliveryTime);

        assertEquals (orderService.createOrder(orderDTO).getBody().getClass(),
                OrderTimeException.class);

    }

    public OrderEntity getSampleOrder(){
        OrderTimeEntity orderTimeEntity = new OrderTimeEntity().setDeliverySlot(ZonedDateTime.parse("2011-12-03T10:35:30+01:00"))
                .setRestaurantAccept(ZonedDateTime.parse("2011-12-03T10:15:30+01:00"));

        List<MenuItemEntity> orderItemsEntities = new ArrayList<>();
        MenuItemEntity menuItemEntity1 = new MenuItemEntity().setName("Sample Item 1").setId(1L);
        MenuItemEntity menuItemEntity2 = new MenuItemEntity().setName("Sample Item 2").setId(2L);
        orderItemsEntities.add(menuItemEntity1);
        orderItemsEntities.add(menuItemEntity2);

        FoodOrderEntity foodOrderEntity = new FoodOrderEntity().setId(1L).setOrderItems(orderItemsEntities).setRestaurantId(1L);
        List<FoodOrderEntity> foodOrderEntities = new ArrayList<>();
        foodOrderEntities.add(foodOrderEntity);

        PriceEntity priceEntity = new PriceEntity().setFood(23.09f);

        OrderEntity orderEntity = new OrderEntity()
                .setDelivery(true).setRefunded(false)
                .setAddress("123 Street St").setOrderTimeEntity(orderTimeEntity)
                .setItems(foodOrderEntities).setPriceEntity(priceEntity);

        return orderEntity;
    }

}