package com.smoothstack.order;

import com.database.ormlibrary.food.MenuItemEntity;
import com.database.ormlibrary.order.FoodOrderEntity;
import com.database.ormlibrary.order.OrderEntity;
import com.database.ormlibrary.order.OrderTimeEntity;
import com.database.ormlibrary.order.PriceEntity;
import com.smoothstack.order.model.CreateResponse;
import com.smoothstack.order.model.Order;
import com.smoothstack.order.model.OrderOrderTime;
import com.smoothstack.order.service.OrderService;
import com.smoothstack.order.exception.OrderTimeException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest (classes = { Main.class })
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Test
    void createOrderTest() throws OrderTimeException {
        //inserts sample items in empty db

        OrderEntity orderEntity = getSampleOrder();

        orderService.insertSampleMenuItems();

        Order orderDTO = orderService.convertToDTO(orderEntity);

        CreateResponse insertedResponse = (CreateResponse) orderService.createOrder(orderDTO).getBody();

        assertEquals (orderDTO.getAddress(), insertedResponse.getAddress());

        OrderOrderTime newDeliveryTime = orderDTO.getOrderTime();

        newDeliveryTime.setDeliverySlot("2011-12-03T10:25:30.000Z");

        orderDTO.setOrderTime(newDeliveryTime);

        assertThrows (OrderTimeException.class, () -> {orderService.createOrder(orderDTO);});

    }

    public OrderEntity getSampleOrder(){
        OrderTimeEntity orderTimeEntity = new OrderTimeEntity()
                .setDeliverySlot(ZonedDateTime.ofInstant(
                        Instant.parse("2011-12-03T10:35:30.000Z"),
                        ZoneOffset.UTC))
                .setRestaurantAccept(ZonedDateTime.ofInstant(
                        Instant.parse("2011-12-03T10:15:30.000Z"),
                        ZoneOffset.UTC));

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