package com.example.eLearningPlatform.controllers.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class LessonUpdateDTO {

    @NotBlank(message = "Lesson title is required")
    private String title;

    @NotBlank(message = "Lesson description is required")
    private String description;

    private MultipartFile video;
}