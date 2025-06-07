package com.example.eLearningPlatform.controllers.dto.respones;

import com.example.eLearningPlatform.models.entities.Comment;
import com.example.eLearningPlatform.models.entities.Resources;
import com.example.eLearningPlatform.models.enums.LessonStatus;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class LessonResponseDTO {

    private Long lessonId;
    private String title;
    private String description;
    private String video;
    private List<CommentsResponseDTO> comments = new ArrayList<>();
    private Long courseId;
    private List<Resources> resources = new ArrayList<>();
    private LocalDateTime createdAt;
    private LessonStatus status;

}
