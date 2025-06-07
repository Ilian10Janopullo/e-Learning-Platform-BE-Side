package com.example.eLearningPlatform.controllers.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import com.example.eLearningPlatform.models.enums.TagType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Data
public class CourseUpdateDTO {

    @NotBlank(message = "Course name is required")
    private String name;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be at least 0")
    @Max(value = 300, message = "Price must be at most 300")
    private Double price;

    private String description;

    private Set<TagType> tags;

    private MultipartFile courseCover;
}
