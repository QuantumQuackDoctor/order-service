package com.smoothstack.order.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.ZonedDateTime;

@ControllerAdvice
@Slf4j(topic = "Order Exception Handler")
public class OrderExceptionHandler {

    @ExceptionHandler(EmptyCartException.class)
    @ResponseBody
    public ResponseEntity<Object> handleEmptyCart(EmptyCartException e) {
        log.error(e.getMessage());
        return ResponseEntity.badRequest().body(
                new OrderExceptionResponse(
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
        log.error(e.getMessage());
        return ResponseEntity.badRequest().body(
                new OrderExceptionResponse(
                        e.getMessage(),
                        e,
                        HttpStatus.BAD_REQUEST,
                        ZonedDateTime.now()
                )
        );
    }

    @ExceptionHandler(MissingFieldsException.class)
    @ResponseBody
    public ResponseEntity<Object> handleMissingFields(MissingFieldsException e) {
        log.error(e.getMessage());
        return ResponseEntity.badRequest().body(
                new OrderExceptionResponse(
                        e.getMessage(),
                        e,
                        HttpStatus.BAD_REQUEST,
                        ZonedDateTime.now()
                )
        );
    }

    @ExceptionHandler(OrderTimeException.class)
    @ResponseBody
    public ResponseEntity<Object> handleTimeException(OrderTimeException e) {
        log.error(e.getMessage());
        return ResponseEntity.badRequest().body(
                new OrderExceptionResponse(
                        e.getMessage(),
                        e,
                        HttpStatus.BAD_REQUEST,
                        ZonedDateTime.now()
                )
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseBody
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity.badRequest().body(
                new OrderExceptionResponse(
                        e.getMessage(),
                        e,
                        HttpStatus.BAD_REQUEST,
                        ZonedDateTime.now()
                )
        );
    }

    @ExceptionHandler(CustomHttpException.class)
    public ResponseEntity<String> handleHttpException(CustomHttpException e){
        return new ResponseEntity<>(e.getMessage(), e.getStatus());
    }
}
