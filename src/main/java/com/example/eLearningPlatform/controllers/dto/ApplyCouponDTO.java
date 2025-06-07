package com.example.eLearningPlatform.controllers.dto;

import lombok.Data;

@Data
public class ApplyCouponDTO {
    private String couponCode;
    private Long courseId;
}
