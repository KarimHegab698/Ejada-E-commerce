package com.example.wallet_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {
    private Long id;
    private String type;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String reference;
    private  String status;
    private LocalDateTime createdAt;
}
