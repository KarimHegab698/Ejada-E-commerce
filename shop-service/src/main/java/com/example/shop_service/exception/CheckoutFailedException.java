package com.example.shop_service.exception;

public class CheckoutFailedException extends RuntimeException{
    public CheckoutFailedException(String message){
        super(message);
    }
}
