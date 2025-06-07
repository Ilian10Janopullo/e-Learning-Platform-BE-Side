package com.example.eLearningPlatform.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(name = "parent_id")
    private Long parentId = null;

    @NotBlank(message = "Comment cannot be empty")
    @Column(nullable = false, name = "content", length = 500)
    private String content;

    @ManyToOne
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "date_commented", nullable = false, updatable = false)
    private LocalDateTime commentedAt;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdatedAt;

    @PrePersist
    protected void onCreate() {
        this.commentedAt = LocalDateTime.now();
        this.lastUpdatedAt = LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {
        this.lastUpdatedAt = LocalDateTime.now();
    }
}

