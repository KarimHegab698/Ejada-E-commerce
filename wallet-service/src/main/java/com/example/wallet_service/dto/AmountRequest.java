package com.example.wallet_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AmountRequest {

    @NotNull
    @DecimalMin(value = "0.01", message = "amount must be greater than zero")
    private BigDecimal amount;
}
