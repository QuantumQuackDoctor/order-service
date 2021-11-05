package com.smoothstack.order.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.database.ormlibrary.driver.DriverEntity;
import com.database.ormlibrary.order.OrderEntity;
import com.database.ormlibrary.order.OrderTimeEntity;
import com.smoothstack.order.exception.OrderNotAcceptedException;
import com.smoothstack.order.exception.OrderNotFoundException;
import com.smoothstack.order.repo.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void PickUpOrder_WithInvalidDriver_ShouldThrowOrderNotAccepted(){
        when(orderRepo.findById(1L)).thenReturn(Optional.of(createSampleOrder()));
        assertThrows(OrderNotAcceptedException.class, () -> driverService.pickUpOrder(1L, 2L));
    }

    @Test
    void PickUpOrder_WithNullDriver_ShouldThrowOrderNotAccepted(){
        when(orderRepo.findById(1L)).thenReturn(Optional.of(createSampleOrder().setDriver(null)));
        assertThrows(OrderNotAcceptedException.class, () -> driverService.pickUpOrder(1L, 2L));
    }

    @Test
    void PickUpOrder_WithNoOrder_ShouldThrowNotFound(){
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
    }

    @Test
    void DeliverOrder_WithInvalidDriver_ShouldThrowOrderNotAccepted(){
        when(orderRepo.findById(1L)).thenReturn(Optional.of(createSampleOrder()));
        assertThrows(OrderNotAcceptedException.class, () -> driverService.deliverOrder(1L, 2L));
    }

    @Test
    void DeliverOrder_WithNullDriver_ShouldThrowOrderNotAccepted(){
        when(orderRepo.findById(1L)).thenReturn(Optional.of(createSampleOrder().setDriver(null)));
        assertThrows(OrderNotAcceptedException.class, () -> driverService.deliverOrder(1L, 2L));
    }

    @Test
    void DeliverOrder_WithNoOrder_ShouldThrowNotFound(){
        when(orderRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> driverService.deliverOrder(1L, 2L));
    }

    OrderEntity createSampleOrder() {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setDelivery(true);
        orderEntity.setOrderTimeEntity(new OrderTimeEntity().setDriverAccept(ZonedDateTime.now().minusHours(1)));
        DriverEntity driver = new DriverEntity();
        driver.setId(1L);
        orderEntity.setDriver(driver);
        orderEntity.setRefunded(false);

        return orderEntity;
    }
}