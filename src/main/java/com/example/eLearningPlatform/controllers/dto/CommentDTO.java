package com.example.eLearningPlatform.controllers.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentDTO {
    @NotBlank(message = "Comment cannot be empty")
    private String content;

    private Long parentId;
}
