package com.example.eLearningPlatform.services.interfaces;

import com.example.eLearningPlatform.models.entities.Admin;
import com.example.eLearningPlatform.models.entities.Lecturer;
import com.example.eLearningPlatform.models.entities.Student;
import com.example.eLearningPlatform.services.interfaces.UserService;

import java.util.List;
import java.util.Optional;

public interface LecturerService extends UserService<Lecturer> {
    Optional<Lecturer> getLecturerByCourseId(Long courseId);
    long countCoursesByLecturerId(Long lecturerId);
    List<Lecturer> getAllLecturers();
}
