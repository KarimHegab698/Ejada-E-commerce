package com.example.wallet_service.security;

import org.springframework.security.core.context.SecurityContextHolder;

public class CurrentUser {
    public static Long id(){
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
