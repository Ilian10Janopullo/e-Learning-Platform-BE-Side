package com.example.eLearningPlatform.controllers.dto;

import com.example.eLearningPlatform.models.enums.AccountStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AccountStatusDTO {
    @NotNull
    private AccountStatus status;
}
