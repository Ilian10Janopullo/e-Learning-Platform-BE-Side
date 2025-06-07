package com.example.eLearningPlatform.controllers.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ResourceCreationDTO {
    @NotBlank(message = "Resource name is required")
    private String name;

    private MultipartFile file;
}
