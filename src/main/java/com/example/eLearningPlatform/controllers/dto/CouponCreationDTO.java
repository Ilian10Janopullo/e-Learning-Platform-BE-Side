package com.example.eLearningPlatform.controllers.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CouponCreationDTO {

    @NotNull(message = "Discount percentage is required")
    @DecimalMin(value = "0.0", message = "Discount percentage must be at least 0")
    @DecimalMax(value = "100.0", message = "Discount percentage must be at most 100")
    private Double discountPercentage;

    @NotNull(message = "Expiration date is required")
    private LocalDateTime expirationDate;

    @Email(message = "Invalid email format!")
    private String studentEmail;
}
