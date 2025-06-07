package com.example.eLearningPlatform.controllers.dto;

import com.example.eLearningPlatform.models.enums.NewsCategory;
import com.example.eLearningPlatform.models.enums.NewsStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateNewsDTO {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    @NotNull(message = "News status is required")
    private NewsStatus newsStatus;

    @NotNull(message = "News category is required")
    private NewsCategory newsCategory;

    // Optional image file upload
    private MultipartFile image;

    // Optional source URL for the news item
    private String sourceUrl;
}