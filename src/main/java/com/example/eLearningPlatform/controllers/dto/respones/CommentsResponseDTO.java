package com.example.eLearningPlatform.controllers.dto.respones;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentsResponseDTO {
    private Long id;
    private Long parentId;
    private String content;
    private Long lessonId;
    private String userName;
    private LocalDateTime updatedAt;
}
