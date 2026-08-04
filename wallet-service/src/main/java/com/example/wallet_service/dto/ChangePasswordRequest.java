package com.example.wallet_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {

    @NotBlank
    private String oldPassword;

    @NotBlank
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$",
            message = "password must be at least 8 characters and include an uppercase letter, a lowercase letter, a digit, and a special character (@#$%^&+=!)"
    )
    private String newPassword;

    @NotBlank
    private String confirmNewPassword;
}
