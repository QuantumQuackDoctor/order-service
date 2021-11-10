package com.smoothstack.order.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.database.ormlibrary.driver.DriverEntity;
import com.database.ormlibrary.food.MenuItemEntity;
import com.database.ormlibrary.food.RestaurantEntity;
import com.database.ormlibrary.order.*;
import com.smoothstack.order.exception.OrderNotAcceptedException;
import com.smoothstack.order.exception.OrderNotFoundException;
import com.smoothstack.order.model.Order;
import com.smoothstack.order.repo.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {DriverService.class, OrderService.class})
class DriverServiceTest {
    @MockBean
    OrderRepo orderRepo;
    @MockBean
    DriverRepo driverRepo;
    @MockBean
    MenuItemRepo menuItemRepo;
    @MockBean
    FoodOrderRepo foodOrderRepo;
    @MockBean
    RestaurantRepo restaurantRepo;
    @MockBean
    UserRepo userRepo;
    @MockBean
    AmazonSimpleEmailService ses;
    @Autowired
    DriverService driverService;

    @Test
    void PickUpOrder_WithValidDriver_ShouldUpdateOrder() throws OrderNotFoundException, OrderNotAcceptedException {
        when(orderRepo.findById(1L)).thenReturn(Optional.of(createSampleOrder()));
        driverService.pickUpOrder(1L, 1L);
        ArgumentCaptor<OrderEntity> orderCaptor = ArgumentCaptor.forClass(OrderEntity.class);
        verify(orderRepo, times(1)).save(orderCaptor.capture());

        assertNotNull(orderCaptor.getValue().getOrderTimeEntity().getDriverPickUp());
    }

    @Test
    void PickUpOrder_WithInvalidDriver_ShouldThrowOrderNotAccepted() {
        when(orderRepo.findById(1L)).thenReturn(Optional.of(createSampleOrder()));
        assertThrows(OrderNotAcceptedException.class, () -> driverService.pickUpOrder(1L, 2L));
    }

    @Test
    void PickUpOrder_WithNullDriver_ShouldThrowOrderNotAccepted() {
        when(orderRepo.findById(1L)).thenReturn(Optional.of(createSampleOrder().setDriver(null)));
        assertThrows(OrderNotAcceptedException.class, () -> driverService.pickUpOrder(1L, 2L));
    }

    @Test
    void PickUpOrder_WithNoOrder_ShouldThrowNotFound() {
        when(orderRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> driverService.pickUpOrder(1L, 2L));
    }

    @Test
    void DeliverOrder_WithValidDriver_ShouldUpdateOrder() throws OrderNotFoundException, OrderNotAcceptedException {
        when(orderRepo.findById(1L)).thenReturn(Optional.of(createSampleOrder()));
        driverService.deliverOrder(1L, 1L);
        ArgumentCaptor<OrderEntity> orderCaptor = ArgumentCaptor.forClass(OrderEntity.class);
        verify(orderRepo, times(1)).save(orderCaptor.capture());

        assertNotNull(orderCaptor.getValue().getOrderTimeEntity().getDriverComplete());
        assertFalse(orderCaptor.getValue().getActive());
    }

    @Test
    void DeliverOrder_WithInvalidDriver_ShouldThrowOrderNotAccepted() {
        when(orderRepo.findById(1L)).thenReturn(Optional.of(createSampleOrder()));
        assertThrows(OrderNotAcceptedException.class, () -> driverService.deliverOrder(1L, 2L));
    }

    @Test
    void DeliverOrder_WithNullDriver_ShouldThrowOrderNotAccepted() {
        when(orderRepo.findById(1L)).thenReturn(Optional.of(createSampleOrder().setDriver(null)));
        assertThrows(OrderNotAcceptedException.class, () -> driverService.deliverOrder(1L, 2L));
    }

    @Test
    void DeliverOrder_WithNoOrder_ShouldThrowNotFound() {
        when(orderRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> driverService.deliverOrder(1L, 2L));
    }

    @Test
    void GetActiveOrders_ShouldReturnOrders() {
        when(orderRepo.getByDriverIdAndActiveTrue(1L)).thenReturn(Collections.singletonList(createSampleOrder()));
        List<Order> orders = driverService.getAcceptedOrders(1L);
        assertEquals(1, orders.size());
    }

    OrderEntity createSampleOrder() {

        OrderTimeEntity orderTimeEntity = new OrderTimeEntity()
                .setDeliverySlot(ZonedDateTime.ofInstant(
                        Instant.parse("2011-12-03T10:35:30.000Z"),
                        ZoneOffset.UTC))
                .setRestaurantAccept(ZonedDateTime.ofInstant(
                        Instant.parse("2011-12-03T10:15:30.000Z"),
                        ZoneOffset.UTC))
                .setDriverAccept(ZonedDateTime.now());

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

        DriverEntity driver = new DriverEntity();
        driver.setId(1L);

        return new OrderEntity()
                .setDelivery(true).setRefunded(false).setDriver(driver).setRefunded(false)
                .setAddress("123 Street St").setOrderTimeEntity(orderTimeEntity)
                .setItems(foodOrderEntities).setPriceEntity(priceEntity).setActive(true);
    }
}