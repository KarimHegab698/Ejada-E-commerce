package com.example.inventory_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String category;
    private String gender;
    private Boolean isNew;
    private Boolean bestSeller;
    private Boolean onSale;
    private BigDecimal displayPrice;
    private String imageUrl;
    private Double averageRating;
    private Integer reviewCount;
    private Integer quantityAvailable;

}
