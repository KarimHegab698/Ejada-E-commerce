package com.example.shop_service.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductInfoResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer quantityAvailable;
}
