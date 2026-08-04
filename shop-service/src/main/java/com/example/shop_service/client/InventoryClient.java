package com.example.shop_service.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @PatchMapping("/stock/{productId}")
    void adjustStock(@PathVariable Long productId,
                     @RequestBody StockAdjustRequest request);
}
