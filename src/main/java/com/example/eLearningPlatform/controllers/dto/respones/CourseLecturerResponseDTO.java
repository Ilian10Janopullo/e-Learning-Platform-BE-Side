package com.example.eLearningPlatform.controllers.dto.respones;

import com.example.eLearningPlatform.models.enums.TagType;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class CourseLecturerResponseDTO {
    private Long id;
    private String name;
    private String lecturer;
    private Double price;
    private String description;
    private LocalDate createdAt;
    private List<ReviewResponseDTO> reviews = new ArrayList<>();
    private Set<TagType> tags = new HashSet<>();
    private byte[] courseCover;
    private double rating;
    private List<LessonResponseDTO> lessons = new ArrayList<>();
}
