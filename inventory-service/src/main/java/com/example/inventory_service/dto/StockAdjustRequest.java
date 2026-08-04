package com.example.inventory_service.dto;

import com.example.inventory_service.entity.Stock;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @Min(1)
    private int quantity;

    @NotBlank
    private String reference;

    @NotNull
    private StockOperation stockOperation;
}
