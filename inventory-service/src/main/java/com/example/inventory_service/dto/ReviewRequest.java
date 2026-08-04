package com.example.inventory_service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {

    @NotBlank
    private String reviewerName;

    @Min(1)
    @Max(5)
    private Integer rating;

    private String comment;
}
