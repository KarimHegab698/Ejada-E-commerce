package com.example.wallet_service.exception;

public class AuthException extends RuntimeException{
    public AuthException(String message){
        super(message);
    }
}
