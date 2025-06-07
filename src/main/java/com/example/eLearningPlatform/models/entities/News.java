package com.example.eLearningPlatform.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import com.example.eLearningPlatform.models.enums.NewsCategory;
import com.example.eLearningPlatform.models.enums.NewsStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "news")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_id")
    private Long id;

    @NotBlank(message = "Title cannot be empty")
    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @NotBlank(message = "Content cannot be empty")
    @Column(length = 5000, name = "content", nullable = false)
    private String content;

    @NotNull
    @Column(name = "news_status")
    @Enumerated(EnumType.STRING)
    private NewsStatus newsStatus;

    @NotNull
    @Column(name = "news_category")
    @Enumerated(EnumType.STRING)
    private NewsCategory newsCategory;

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin createdBy;

    @Lob
    @Column(name = "image", columnDefinition = "LONGBLOB")
    private byte[] image;

    @Column(name = "source_url")
    private String sourceUrl;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
