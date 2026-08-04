package com.example.shop_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "wallet-service")
public interface WalletClient {

    @GetMapping("/wallets/user/{userId}")
    WalletInfoResponse getWalletByUserId(@PathVariable("userId") Long userId);

    @PostMapping("wallets/{walletId}/pay")
    WalletPaymentResponse pay(@PathVariable("walletId") Long walletId, @RequestBody WalletPaymentRequest request);
}
