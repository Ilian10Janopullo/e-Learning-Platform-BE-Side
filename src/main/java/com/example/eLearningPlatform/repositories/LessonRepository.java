package com.example.eLearningPlatform.repositories;

import com.example.eLearningPlatform.models.entities.Lesson;
import com.example.eLearningPlatform.models.enums.LessonStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findByCourseId(Long courseId);// Get all lessons for a course

    @Query("SELECT l FROM Lesson l WHERE l.course.id = :courseId AND l.status = 'ACCEPTED'")
    List<Lesson> findAcceptedLessonsByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT l FROM Lesson l WHERE l.status = 'PENDING'")
    List<Lesson> findPendingLessons();

    Optional<Lesson> findByTitle(String title); // Find a lesson by title

    List<Lesson> findLessonByStatus(LessonStatus status);

}
