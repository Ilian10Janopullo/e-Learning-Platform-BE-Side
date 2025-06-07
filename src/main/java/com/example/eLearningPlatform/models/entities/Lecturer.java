package com.example.eLearningPlatform.models.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "lecturers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@PrimaryKeyJoinColumn(name = "lecturer_id", referencedColumnName = "user_id")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id")

public class Lecturer extends User {

    @Column(columnDefinition = "TEXT", name = "lecturer_description")
    @NotBlank(message = "Description is required")
    private String description;

    //If we delete a lecturer we do not want top delete a course because of the student who may have purchased it
    @OneToMany(mappedBy = "lecturer", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private Set<Course> courses = new HashSet<>();

    @Column(columnDefinition = "TEXT", name = "stripe_account_id")
    private String stripeAccountId;

    public double getAverageRating() {
        if (courses == null || courses.isEmpty()) {
            return 0.0; // No courses means no rating
        }
        return courses.stream()
                .filter(course -> course.getAverageRating() > 0)
                .mapToDouble(Course::getAverageRating)
                .average()
                .orElse(0.0);
    }
}
