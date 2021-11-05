package com.smoothstack.order.exception;

import org.springframework.http.HttpStatus;

public class OrderNotAcceptedException extends CustomHttpException{

    public OrderNotAcceptedException() {
        super(HttpStatus.CONFLICT, "Order not accepted by driver");
    }
}
