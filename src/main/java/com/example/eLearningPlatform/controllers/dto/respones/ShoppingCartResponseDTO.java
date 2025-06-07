package com.example.eLearningPlatform.controllers.dto.respones;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class ShoppingCartResponseDTO {
    private Long id;
    private Long studentId;
    private Set<CourseResponseDTO> courseResponseDTOSet = new HashSet<>();
}
