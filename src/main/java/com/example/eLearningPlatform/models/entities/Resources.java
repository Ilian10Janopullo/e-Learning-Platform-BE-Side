package com.example.eLearningPlatform.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "resources")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Resources {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resource_id")
    private Long id;

    @NotBlank(message = "Resource name is required")
    @Column(nullable = false, name = "resource_name")
    private String name;

    @Lob
    @Column(name = "resource_file", columnDefinition = "LONGBLOB")
    private byte[] file;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;
}
