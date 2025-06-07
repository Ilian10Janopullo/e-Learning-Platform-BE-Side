package com.example.eLearningPlatform.controllers.dto.respones;

import com.example.eLearningPlatform.models.enums.FlaggedType;
import lombok.Data;

@Data
public class FlagResponseDTO {
    private Long id;
    private FlaggedType flaggedType;
    private String reason;
    private String content;
    private String courseName;
    private String lessonName;
}
