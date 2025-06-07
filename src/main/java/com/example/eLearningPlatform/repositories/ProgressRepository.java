package com.example.eLearningPlatform.repositories;

import com.example.eLearningPlatform.models.entities.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, Long> {

    Optional<Progress> findByStudentIdAndCourseId(Long studentId, Long courseId);
    List<Progress> findByStudentId(Long studentId);
    List<Progress> findByCourseId(Long courseId);
    @Query("SELECT COUNT(p) FROM Progress p WHERE p.course.id = :courseId AND p.completed = true")
    long countCompletedStudentsByCourseId(@Param("courseId") Long courseId);
    @Query("SELECT AVG(p.progressPercentage) FROM Progress p WHERE p.course.id = :courseId")
    Double getAverageProgressForCourse(@Param("courseId") Long courseId);
    List<Progress> findByStudentIdAndCompletedTrue(Long studentId);
    @Query("SELECT COUNT(p) FROM Progress p WHERE p.course.id = :courseId")
    long countTotalStudentsInCourse(@Param("courseId") Long courseId);

    List<Progress> findByProgressPercentageLessThan(Double progress);
}
