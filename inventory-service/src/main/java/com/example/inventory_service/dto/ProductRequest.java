package com.example.inventory_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal price;

    private BigDecimal discountPercentage;

    private String category;

    private String gender;

    private Boolean isNew = true;

    private Boolean bestSeller = false;

    private Boolean onSale = false;

    private String imageUrl;

    private Integer initialQuantity =0;
}
