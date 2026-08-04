package com.example.shop_service.service;

import com.example.shop_service.client.InventoryClient;
import com.example.shop_service.client.StockAdjustRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

@Service
public class InventoryGateway {

    private final InventoryClient inventoryClient;

    public InventoryGateway(InventoryClient inventoryClient) {
        this.inventoryClient = inventoryClient;
    }

    @CircuitBreaker(name = "inventoryService", fallbackMethod = "adjustStockFallback")
    public void adjustStock(Long productId, StockAdjustRequest request) {
        inventoryClient.adjustStock(productId, request);
    }

    private void adjustStockFallback(Long productId,
                                     StockAdjustRequest request,
                                     Throwable t) {

        switch (request.getStockOperation()) {
            case DEDUCT ->
                    throw new RuntimeException(
                            "Inventory service unavailable, cannot deduct stock for product " + productId
                                    + " [" + t.getClass().getSimpleName() + ": " + t.getMessage() + "]", t);

            case RESTORE ->
                    System.err.println(
                            "Inventory service unavailable, could not restore stock for product "
                                    + productId + " (reference " + request.getReference()
                                    + ") [" + t.getClass().getSimpleName() + ": " + t.getMessage() + "]");
        }
    }
}
