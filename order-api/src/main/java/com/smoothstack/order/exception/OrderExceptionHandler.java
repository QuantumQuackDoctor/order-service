package com.smoothstack.order.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.time.ZonedDateTime;

@ControllerAdvice
public class OrderExceptionHandler {

    @ExceptionHandler(EmptyCartException.class)
    @ResponseBody
    public ResponseEntity<Object> handleEmptyCart (EmptyCartException e){
        return ResponseEntity.badRequest().body(
                new OrderException(
                        e.getMessage(),
                        e,
                        HttpStatus.BAD_REQUEST,
                        ZonedDateTime.now()
                )
        );
    }

    @ExceptionHandler(ValueNotPresentException.class)
    @ResponseBody
    public ResponseEntity<Object> handleNotFound(ValueNotPresentException e) {
        return ResponseEntity.badRequest().body(
                new OrderException(
                        e.getMessage(),
                        e,
                        HttpStatus.BAD_REQUEST,
                        ZonedDateTime.now()
                )
        );
    }

    @ExceptionHandler(MissingFieldsException.class)
    @ResponseBody
    public ResponseEntity<Object> handleMissingFields (MissingFieldsException e){
        return ResponseEntity.badRequest().body(
                new OrderException(
                        e.getMessage(),
                        e,
                        HttpStatus.BAD_REQUEST,
                        ZonedDateTime.now()
                )
        );
    }

    @ExceptionHandler(OrderTimeException.class)
    @ResponseBody
    public ResponseEntity<Object> handleTimeException (OrderTimeException e){
        return ResponseEntity.badRequest().body(
                new OrderException(
                        e.getMessage(),
                        e,
                        HttpStatus.BAD_REQUEST,
                        ZonedDateTime.now()
                )
        );
    }

}
