package com.smoothstack.order.service;

import com.database.ormlibrary.food.MenuItemEntity;
import com.database.ormlibrary.order.OrderEntity;
import com.smoothstack.order.model.OrderFood;
import com.smoothstack.order.model.OrderItems;
import com.smoothstack.order.repo.DriverRepo;
import com.smoothstack.order.repo.MenuItemRepo;
import com.smoothstack.order.repo.OrderRepo;
import com.smoothstack.order.model.CreateResponse;
import com.smoothstack.order.model.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith (SpringExtension.class)
@SpringBootTest (classes = OrderService.class)
class OrderServiceTest {

    @MockBean (MenuItemRepo.class)
    private MenuItemRepo menuItemRepo;

    @MockBean (OrderRepo.class)
    private OrderRepo orderRepo;

    @MockBean (DriverRepo.class)
    private DriverRepo driverRepo;

    @Autowired
    private OrderService orderService;


    @Test
    void createOrderTest(){

        OrderItems item1 = new OrderItems().name("Item 1").setId("1");
        OrderItems item2 = new OrderItems().name("Item 2").setId("2");

        List<OrderItems> orderItems = new ArrayList<>();
        orderItems.add(item1);
        orderItems.add(item2);

        MenuItemEntity item1Ent = new MenuItemEntity().setId(1L).setName("Item 1");
        MenuItemEntity item2Ent = new MenuItemEntity().setId(2L).setName("Item 2");

        OrderFood orderFood = new OrderFood().restaurantId("1")
                .items(orderItems);

        List<OrderFood> food = new ArrayList<>();
        food.add(orderFood);

        Order orderDTO = new Order();
        orderDTO.orderType(Order.OrderTypeEnum.DELIVERY).driverId("1").restaurantId("1").food(food)
        .address("123 Street st").driverNote("Test driver note").refunded(false).id("23");

        OrderEntity orderEntity = new OrderEntity().setDelivery(true).setActive(true).setRefunded(false)
                .setDriverNote("Test driver note").setId(23L);

        CreateResponse createResponse = new CreateResponse().id("23").type(CreateResponse.TypeEnum.STRIPE);

        Mockito.when (orderRepo.save(Mockito.any())).thenReturn(orderEntity);
        assertEquals(createResponse, orderService.createOrder(orderDTO).getBody());

    }
}