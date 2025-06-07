package com.example.eLearningPlatform.controllers.dto.respones;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewResponseDTO {
    private Long id;
    private String content;
    private Integer rating;
    private String studentUsername;
    private LocalDateTime lastUpdatedAt;
    private Long courseId;
}
