package com.example.eLearningPlatform.services.interfaces;

import com.example.eLearningPlatform.models.entities.Admin;
import com.example.eLearningPlatform.models.entities.Course;
import com.example.eLearningPlatform.models.entities.Student;
import com.example.eLearningPlatform.services.interfaces.UserService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface StudentService extends UserService<Student> {
    List<Student> getStudentsByCourseId(Long courseId);
    List<Student> getStudentsWithCertificates();
    void enrollStudentInCourse(Long studentId, Long courseId);
    void addPurchasedCourses(Long studentId, Set<Course> courses);
    List<Student> getAllStudents();
}
