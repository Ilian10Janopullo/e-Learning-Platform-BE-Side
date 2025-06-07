package com.example.eLearningPlatform.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import com.example.eLearningPlatform.models.enums.LessonStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lessons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lesson_id")
    private Long id;

    @NotBlank(message = "Lesson title is required")
    @Column(nullable = false, name = "title")
    private String title;

    @NotBlank(message = "Lesson description is required")
    @Column(nullable = false, name = "lesson_description", length = 1000)
    private String description;

    @Column(nullable = false, name = "lesson_status")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Lesson Status is invalid!")
    private LessonStatus status = LessonStatus.PENDING;

    @Column(name = "video")
    private String video;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resources> resources = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
