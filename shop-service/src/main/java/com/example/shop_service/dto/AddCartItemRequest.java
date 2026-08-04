package com.example.shop_service.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCartItemRequest {

    @NonNull
    private Long productId;

    @Min(1)
    private int quantity;
}
