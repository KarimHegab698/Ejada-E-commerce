package com.example.shop_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {
    private Long id;
    private Long userId;
    private String status;
    private List<CartItemResponse> items;
    private BigDecimal total;
}
