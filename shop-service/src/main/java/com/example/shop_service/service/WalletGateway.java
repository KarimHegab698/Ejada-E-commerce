package com.example.shop_service.service;

import com.example.shop_service.client.WalletClient;
import com.example.shop_service.client.WalletInfoResponse;
import com.example.shop_service.client.WalletPaymentRequest;
import com.example.shop_service.client.WalletPaymentResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

@Service
public class WalletGateway {

    private final WalletClient walletClient;

    public WalletGateway(WalletClient walletClient) {
        this.walletClient = walletClient;
    }

    @CircuitBreaker(name = "walletService", fallbackMethod = "getWalletFallback")
    public WalletInfoResponse getWalletByUserId(Long userId) {
        return walletClient.getWalletByUserId(userId);
    }

    @CircuitBreaker(name = "walletService", fallbackMethod = "payFallback")
    public WalletPaymentResponse pay(Long walletId, WalletPaymentRequest request) {
        return walletClient.pay(walletId, request);
    }

    private WalletInfoResponse getWalletFallback(Long userId, Throwable t) {
        return null;
    }

    private WalletPaymentResponse payFallback(Long walletId, WalletPaymentRequest request, Throwable t) {
        return new WalletPaymentResponse(null, "FAILED", null,
                t.getClass().getSimpleName() + ": " + t.getMessage());
    }
}