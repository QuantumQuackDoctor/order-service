package com.smoothstack.order.service;

import com.database.ormlibrary.order.OrderEntity;
import com.smoothstack.order.exception.OrderNotAcceptedException;
import com.smoothstack.order.exception.OrderNotFoundException;
import com.smoothstack.order.model.Order;
import com.smoothstack.order.repo.OrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriverService {
    private final OrderRepo orderRepo;
    private final OrderService orderService;

    public void pickUpOrder(Long orderId, Long driverId) throws OrderNotFoundException, OrderNotAcceptedException {
        OrderEntity order = orderRepo.findById(orderId).orElseThrow(
                () -> new OrderNotFoundException("order does not exist"));

        if (orderValid(order, driverId)) {
            order.getOrderTimeEntity().setDriverPickUp(ZonedDateTime.now());
            orderRepo.save(order);
        } else {
            throw new OrderNotAcceptedException();
        }
    }

    public List<Order> getAcceptedOrders(Long driverId) {
        List<OrderEntity> driverOrders = orderRepo.getOrderEntitiesByDriverId(driverId);
        return driverOrders.stream().map((orderService::convertToDTO)).collect(Collectors.toList());
    }

    public void deliverOrder(Long orderId, Long driverId) throws OrderNotFoundException, OrderNotAcceptedException {
        OrderEntity order = orderRepo.findById(orderId).orElseThrow(
                () -> new OrderNotFoundException("order does not exist"));

        if (orderValid(order, driverId)) {
            if (order.getOrderTimeEntity().getDriverPickUp() == null)
                order.getOrderTimeEntity().setDriverPickUp(ZonedDateTime.now());
            order.getOrderTimeEntity().setDriverComplete(ZonedDateTime.now());
            order.setActive(false);
            orderRepo.save(order);
        } else {
            throw new OrderNotAcceptedException();
        }
    }

    private boolean orderValid(OrderEntity order, Long driverId){
        return order.getActive() && order.getDriver() != null && order.getDriver().getId().equals(driverId);
    }
}
