package com.smoothstack.order.service;

import com.database.ormlibrary.food.MenuItemEntity;
import com.database.ormlibrary.order.OrderEntity;
import com.smoothstack.order.model.*;
import com.smoothstack.order.repo.DriverRepo;
import com.smoothstack.order.repo.MenuItemRepo;
import com.smoothstack.order.repo.OrderRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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

    @Captor
    ArgumentCaptor<OrderEntity> orderCaptor;

    @Captor
    ArgumentCaptor<MenuItemEntity> menuItemCaptor;

    @Autowired
    private OrderService orderService;

    @Test
    void createOrderTest(){
        Mockito.when (orderRepo.save(orderCaptor.capture())).thenReturn (null);
        Mockito.when (menuItemRepo.save(menuItemCaptor.capture())).thenReturn(null);

        //items to add
        OrderItems item1 = new OrderItems().name("Item 1").setId("1");
        OrderItems item2 = new OrderItems().name("Item 2").setId("2");

        List<OrderItems> orderItems = new ArrayList<>();
        orderItems.add(item1);
        orderItems.add(item2);

        OrderFood orderFood = new OrderFood().restaurantId("1")
                .items(orderItems);
        List<OrderFood> food = new ArrayList<>();
        food.add(orderFood);
        OrderOrderTime orderOrderTime = new OrderOrderTime().deliverySlot("2011-12-03T10:15:30+01:00")
                .restaurantAccept("2011-12-03T10:35:30+01:00");

        Order orderDTO = new Order();
        orderDTO.orderType(Order.OrderTypeEnum.DELIVERY).restaurantId("1").food(food)
        .address("123 Street st").driverNote("Test driver note").refunded(false).id("23")
        .setOrderTime(orderOrderTime);

        OrderEntity orderEntity = new OrderEntity().setDelivery(true).setActive(true).setRefunded(false)
                .setDriverNote("Test driver note").setId(23L);

        CreateResponse createResponse = new CreateResponse().id("23").type(CreateResponse.TypeEnum.STRIPE);
        CreateResponse testResponse = (CreateResponse) orderService.createOrder(orderDTO).getBody();

        OrderEntity createdOrder = orderCaptor.getValue();
        MenuItemEntity itemEntity2 = menuItemCaptor.getValue();

        //assertions
        assertEquals(createResponse, testResponse);
        assertEquals(23L, createdOrder.getId());
        assertTrue(createdOrder.getDelivery());
        assertNull (createdOrder.getDriver());
        assertEquals("Test driver note", createdOrder.getDriverNote());
        assertTrue (createdOrder.getActive());
        assertEquals(itemEntity2.getId(), 2L);
        assertEquals(itemEntity2.getName(), "Item 2");
    }
}