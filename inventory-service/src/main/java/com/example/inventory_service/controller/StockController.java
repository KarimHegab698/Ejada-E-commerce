package com.example.inventory_service.controller;


import com.example.inventory_service.dto.StockAdjustRequest;
import com.example.inventory_service.dto.StockResponse;
import com.example.inventory_service.service.StockService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stock")
public class StockController {
    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/{productId}")
    public StockResponse getStock(@PathVariable Long productId){
        return stockService.getStock(productId);
    }

    @PatchMapping("/{productId}")
    public void adjustStock(
            @PathVariable Long productId,
            @Valid @RequestBody StockAdjustRequest request) {

        stockService.adjustStock(productId, request);
    }
}
