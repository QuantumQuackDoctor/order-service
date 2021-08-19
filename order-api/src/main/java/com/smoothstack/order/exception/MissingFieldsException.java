package com.smoothstack.order.exception;
public class MissingFieldsException extends Exception{
    public MissingFieldsException (String errMessage){
        super (errMessage);
    }
}
