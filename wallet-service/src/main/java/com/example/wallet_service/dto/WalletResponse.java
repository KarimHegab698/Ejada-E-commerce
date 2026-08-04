package com.example.wallet_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletResponse {
    private Long id;
    private Long userId;
    private BigDecimal balance;
    private String currency;
}
