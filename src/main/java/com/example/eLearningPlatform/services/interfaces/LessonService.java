package com.example.eLearningPlatform.services.interfaces;

import com.example.eLearningPlatform.models.entities.Lesson;
import com.example.eLearningPlatform.models.enums.LessonStatus;

import java.util.List;

public interface LessonService {
    Lesson createLesson(Long courseId, Lesson lesson);
    List<Lesson> getLessonsByCourseId(Long coursedId);
    List<Lesson> getLessonsByStatus(LessonStatus status);
    List<Lesson> getAcceptedLessonsByCourseId(Long courseId);
    List<Lesson> getPendingLessons();
    Lesson getLessonById(Long id);
    Lesson getLessonByTitle(String title);
    Lesson updateLesson(Lesson lesson);
    void deleteLesson(Long id);
}

