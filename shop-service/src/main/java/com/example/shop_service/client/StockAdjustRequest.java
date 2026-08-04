package com.example.shop_service.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockAdjustRequest {

    public enum StockOperation {
        DEDUCT,
        RESTORE
    }

    private int quantity;
    private String reference;
    private StockOperation stockOperation;
}