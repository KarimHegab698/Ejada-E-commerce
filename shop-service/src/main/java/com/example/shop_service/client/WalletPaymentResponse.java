package com.example.shop_service.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletPaymentResponse {
    private Long transactionId;
    private String status;
    private BigDecimal balanceAfter;
    private String message;
}
