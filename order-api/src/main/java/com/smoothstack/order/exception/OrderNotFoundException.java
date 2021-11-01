package com.smoothstack.order.exception;

import org.springframework.http.HttpStatus;

public class OrderNotFoundException extends CustomHttpException{
    public OrderNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
