package com.smoothstack.order.service;

import com.database.ormlibrary.order.OrderEntity;
import com.smoothstack.order.exception.OrderNotAcceptedException;
import com.smoothstack.order.exception.OrderNotFoundException;
import com.smoothstack.order.repo.OrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class DriverService {
    private final OrderRepo orderRepo;

    public void pickUpOrder(Long orderId, Long driverId) throws OrderNotFoundException, OrderNotAcceptedException {
        OrderEntity order = orderRepo.findById(orderId).orElseThrow(
                () -> new OrderNotFoundException("order does not exist"));

        if (order.getDriver() != null && order.getDriver().getId().equals(driverId)) {
            order.getOrderTimeEntity().setDriverPickUp(ZonedDateTime.now());
            orderRepo.save(order);
        } else {
            throw new OrderNotAcceptedException();
        }
    }
}
