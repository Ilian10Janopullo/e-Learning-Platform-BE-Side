package com.example.eLearningPlatform.models.entities;

import jakarta.persistence.*;
import lombok.*;
import com.example.eLearningPlatform.models.enums.FlaggedType;

import java.time.LocalDateTime;

@Entity
@Table(name = "flags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Flag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flag_id")
    private Long id;

    @Column(name = "flagged_by_id", nullable = false)
    private Long userId;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "content_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private FlaggedType contentType;

    @Column(name = "object_id", nullable = false)
    private Long objectId;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
