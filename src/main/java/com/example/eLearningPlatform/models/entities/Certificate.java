package com.example.eLearningPlatform.models.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "certificates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "certificate_id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @OneToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "date_issued")
    private LocalDateTime issuedAt;

    @Column(name = "certificate_pdf")
    private byte[] certificatePdf;

    @PrePersist
    protected void onCreate() {
        this.issuedAt = LocalDateTime.now();
    }
}
