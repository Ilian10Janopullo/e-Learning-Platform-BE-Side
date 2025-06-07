package com.example.eLearningPlatform.models.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "coupon_id")
    private String couponCode;

    @Column(name = "discount_percentage", nullable = false)
    private Double discountPercentage;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "lecturer_id", nullable = false)
    private Lecturer lecturer;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = true)
    private Student student;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
