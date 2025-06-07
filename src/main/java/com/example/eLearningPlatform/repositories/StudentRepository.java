package com.example.eLearningPlatform.repositories;

import com.example.eLearningPlatform.models.entities.Course;
import com.example.eLearningPlatform.models.entities.Student;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends UserRepository<Student> {
    @Query("SELECT s FROM Student s JOIN s.courses c WHERE c.id = :courseId")
    List<Student> findStudentsByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT s FROM Student s WHERE SIZE(s.certificates) > 0")
    List<Student> findStudentsWithCertificates();

    List<Student> findByCoursesNotContaining(Course course);
}
