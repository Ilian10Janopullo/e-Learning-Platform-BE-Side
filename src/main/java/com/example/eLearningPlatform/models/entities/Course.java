package com.example.eLearningPlatform.models.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import com.example.eLearningPlatform.models.enums.TagType;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long id;

    @NotBlank(message = "Course name is required")
    @Column(name = "course_name")
    private String name;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "lecturer_id")
    private Lecturer lecturer;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "student_courses",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private Set<Student> students = new HashSet<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lesson> lessons;

    @NotNull(message = "Price is required")
    @Max(300)
    @Min(0)
    @Column(name = "price")
    private Double price;

    @Column(columnDefinition = "TEXT", name = "course_description")
    private String description;

    @Column(nullable = false, updatable = false, name = "date_created")
    private LocalDate createdAt;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

    @ElementCollection(targetClass = TagType.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "course_tags", joinColumns = @JoinColumn(name = "course_id"))
    @Enumerated(EnumType.STRING)
    private Set<TagType> tags = new HashSet<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Progress> progressRecords = new HashSet<>();

    @Column(name = "course_cover", columnDefinition = "LONGBLOB")
    @Lob
    private byte[] courseCover;

    public double getAverageRating() {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }
        return reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
    }
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDate.now();
    }
}
