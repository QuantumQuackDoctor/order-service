package com.smoothstack.order.exception;

public class EmptyCartException extends Exception{

    public EmptyCartException(String message) {
        super(message);
    }

    public EmptyCartException(String message, Throwable cause) {
        super(message, cause);
    }
}
