package com.smoothstack.order.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public abstract class CustomHttpException extends Exception{
    private final HttpStatus status;
    private final String message;
}
