package com.example.eLearningPlatform.repositories;

import com.example.eLearningPlatform.models.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByCourseId(Long courseId);

    List<Review> findByStudentId(Long studentId);

    Optional<Review> findByStudentIdAndCourseId(Long studentId, Long courseId);

    long countByCourseId(Long courseId);
}
