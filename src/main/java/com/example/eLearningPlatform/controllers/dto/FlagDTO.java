package com.example.eLearningPlatform.controllers.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import com.example.eLearningPlatform.models.enums.FlaggedType;

@Data
public class FlagDTO {
    @NotBlank(message = "Reason is required")
    private String reason;

    @NotNull(message = "Content type is required")
    private FlaggedType contentType;

    @NotNull(message = "Object ID is required")
    private Long objectId;
}
