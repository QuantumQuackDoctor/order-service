package com.smoothstack.order.service;

import com.database.ormlibrary.food.MenuItemEntity;
import com.database.ormlibrary.food.RestaurantEntity;
import com.database.ormlibrary.order.*;
import com.database.ormlibrary.user.UserEntity;
import com.smoothstack.order.exception.OrderTimeException;
import com.smoothstack.order.exception.UserNotFoundException;
import com.smoothstack.order.exception.ValueNotPresentException;
import com.smoothstack.order.model.CreateResponse;
import com.smoothstack.order.model.Order;
import com.smoothstack.order.model.OrderOrderTime;
import com.smoothstack.order.repo.OrderRepo;
import com.smoothstack.order.repo.RestaurantRepo;
import com.smoothstack.order.repo.UserRepo;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest
class OrderServiceTest {

    @MockBean(OrderRepo.class)
    OrderRepo orderRepo;

    @MockBean(RestaurantRepo.class)
    RestaurantRepo restaurantRepo;

    @MockBean(UserRepo.class)
    UserRepo userRepo;

    @Autowired
    private OrderService orderService;

    @Test
    void createOrderTest() throws OrderTimeException, UserNotFoundException {

        OrderEntity orderEntity = getSampleOrder();

        Mockito.when(userRepo.findById(anyLong())).thenReturn(sampleUser());
        Mockito.when(orderRepo.save(Mockito.any())).thenReturn(getSampleOrder());
        Mockito.when(restaurantRepo.findById(Mockito.any())).thenReturn(Optional.of(new RestaurantEntity().setName("Sample Restaurant").setId(1L)));
        //inserts sample items in empty db

        Order orderDTO = orderService.convertToDTO(orderEntity);

        CreateResponse insertedResponse = orderService.createOrder(orderDTO, 500L).getBody();

        assert insertedResponse != null;
        assertEquals(orderDTO.getAddress(), insertedResponse.getAddress());

        OrderOrderTime newDeliveryTime = orderDTO.getOrderTime();

        newDeliveryTime.setDeliverySlot("2011-12-03T10:25:30.000Z");

        orderDTO.setOrderTime(newDeliveryTime);

        assertThrows(OrderTimeException.class, () -> orderService.createOrder(orderDTO, 500L));

    }

    @Test
    void getUserOrderNormal() throws UserNotFoundException {
        Mockito.when(userRepo.findById(anyLong())).thenReturn(sampleUser());
        List<Order> orderList = orderService.getUserOrders(1L);
        assertEquals(0, orderList.size());
    }

    @Test
    void getUserOrderException() {
        assertThrows(UserNotFoundException.class, () -> orderService.getUserOrders(1L));
    }

    @Test
    void createDeleteOrderTest() throws OrderTimeException, UserNotFoundException {
        OrderEntity orderEntity = getSampleOrder();

        Mockito.when(userRepo.findById(anyLong())).thenReturn(sampleUser());
        Mockito.when(orderRepo.save(Mockito.any())).thenReturn(getSampleOrder());
        Mockito.when(restaurantRepo.findById(Mockito.any())).thenReturn(Optional.of(new RestaurantEntity().setName("Sample Restaurant")));

        Order orderDTO = orderService.convertToDTO(orderEntity);

        CreateResponse insertedResponse = orderService.createOrder(orderDTO, 500L).getBody();

        assert insertedResponse != null;
        assertEquals(orderDTO.getAddress(), insertedResponse.getAddress());

        orderService.deleteOrder(500L);

        assertThrows(ValueNotPresentException.class, () -> orderService.getOrder(500L));
    }

    @Test
    void getOrderNormal() throws ValueNotPresentException {
        OrderEntity orderEntity = getSampleOrder();
        Order orderDTO = orderService.convertToDTO(orderEntity);

        Mockito.when(userRepo.findById(anyLong())).thenReturn(sampleUser());
        Mockito.when(orderRepo.findById(any())).thenReturn(Optional.of(getSampleOrder()));

        assertEquals(orderDTO.getId(), Objects.requireNonNull(orderService.getOrder(500L).getBody()).getId());
    }

    @Test
    void getActiveOrdersNormal () {
        Iterable<OrderEntity> orderEntityIterable = Collections.singletonList(getSampleOrder());

        Mockito.when (orderRepo.findAll()).thenReturn(orderEntityIterable);

        assertEquals(Objects.requireNonNull(orderService.getActiveOrders("time", 0, 1).getBody()).size(), 1);
        assertEquals(Objects.requireNonNull(orderService.getActiveOrders("price", 0, 1).getBody()).size(), 1);
    }

    public Optional<UserEntity> sampleUser() {
        UserEntity user = new UserEntity();

        return Optional.of(user);
    }

    public OrderEntity getSampleOrder() {
        OrderTimeEntity orderTimeEntity = new OrderTimeEntity()
                .setDeliverySlot(ZonedDateTime.ofInstant(
                        Instant.parse("2011-12-03T10:35:30.000Z"),
                        ZoneOffset.UTC))
                .setRestaurantAccept(ZonedDateTime.ofInstant(
                        Instant.parse("2011-12-03T10:15:30.000Z"),
                        ZoneOffset.UTC));

        MenuItemEntity menuItemEntity1 = new MenuItemEntity().setName("Sample Item 1").setId(1L);
        MenuItemEntity menuItemEntity2 = new MenuItemEntity().setName("Sample Item 2").setId(2L);

        RestaurantEntity restaurantEntity1 = new RestaurantEntity().setId(1L);
        RestaurantEntity restaurantEntity2 = new RestaurantEntity().setId(2L);

        OrderConfigurationEntity orderConfigurationEntity1 = new OrderConfigurationEntity().setConfigurationName("1 quantity: 1");
        OrderConfigurationEntity orderConfigurationEntity2 = new OrderConfigurationEntity().setConfigurationName("2 quantity: 1");

        FoodOrderEntity foodOrderEntity1 = new FoodOrderEntity().setId(1L).setMenuItem(menuItemEntity1)
                .setRestaurant(restaurantEntity1).setConfigurations(Collections.singletonList(orderConfigurationEntity1));
        FoodOrderEntity foodOrderEntity2 = new FoodOrderEntity().setId(2L).setMenuItem(menuItemEntity2)
                .setRestaurant(restaurantEntity2).setConfigurations(Collections.singletonList(orderConfigurationEntity2));
        List<FoodOrderEntity> foodOrderEntities = new ArrayList<>();
        foodOrderEntities.add(foodOrderEntity1);
        foodOrderEntities.add(foodOrderEntity2);

        PriceEntity priceEntity = new PriceEntity().setFood(23.09f);

        return new OrderEntity()
                .setDelivery(true).setRefunded(false)
                .setAddress("123 Street St").setOrderTimeEntity(orderTimeEntity)
                .setItems(foodOrderEntities).setPriceEntity(priceEntity);
    }

}