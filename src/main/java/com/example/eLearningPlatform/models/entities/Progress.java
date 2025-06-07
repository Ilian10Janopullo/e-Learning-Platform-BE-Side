package com.example.eLearningPlatform.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "progress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Progress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "progress_percentage")
    @Min(0)
    @Max(100)
    private double progressPercentage;

    @Column(name = "is_completed")
    private boolean completed;

    @Column(name = "started_date")
    private LocalDateTime startedAt;

    @Column(name = "last_date")
    private LocalDateTime lastUpdated;

    @ElementCollection
    @CollectionTable(
            name = "completed_lessons",
            joinColumns = @JoinColumn(name = "progress_id")
    )
    @Column(name = "lesson_id")
    private Set<Long> completedLessonIds = new HashSet<>();

    public void setProgressPercentage(double progressPercentage) {
        this.progressPercentage = Math.max(0, Math.min(100, progressPercentage));
        this.completed = (progressPercentage == 100);
    }

    @PrePersist
    protected void onCreate() {
        this.startedAt = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }
}
