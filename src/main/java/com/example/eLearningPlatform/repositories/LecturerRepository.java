package com.example.eLearningPlatform.repositories;

import com.example.eLearningPlatform.models.entities.Lecturer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LecturerRepository extends UserRepository<Lecturer> {
    @Query("SELECT l FROM Lecturer l JOIN l.courses c WHERE c.id = :courseId")
    Optional<Lecturer> findLecturersByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT COUNT(c) FROM Lecturer l JOIN l.courses c WHERE l.id = :lecturerId")
    long countCoursesByLecturerId(@Param("lecturerId") Long lecturerId);

}
