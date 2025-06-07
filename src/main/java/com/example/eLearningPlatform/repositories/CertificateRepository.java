package com.example.eLearningPlatform.repositories;

import com.example.eLearningPlatform.models.entities.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, UUID> {

    List<Certificate> findByStudentId(Long studentId);

    Optional<Certificate> findByStudentIdAndCourseId(Long studentId, Long courseId);

    long countByStudentId(Long studentId);

    long countByCourseId(Long courseId);
}
