package com.example.eLearningPlatform.services.classes;

import com.example.eLearningPlatform.models.entities.Course;
import com.example.eLearningPlatform.models.entities.Lesson;
import com.example.eLearningPlatform.models.enums.LessonStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.example.eLearningPlatform.repositories.CourseRepository;
import com.example.eLearningPlatform.repositories.LessonRepository;
import com.example.eLearningPlatform.services.interfaces.LessonService;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public LessonServiceImpl(LessonRepository lessonRepository, CourseRepository courseRepository) {
        this.lessonRepository = lessonRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public Lesson createLesson(Long courseId, Lesson lesson) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        lesson.setCourse(course);
        return lessonRepository.save(lesson);
    }

    @Override
    public List<Lesson> getLessonsByCourseId(Long courseId) {
        return lessonRepository.findByCourseId(courseId);
    }

    @Override
    public List<Lesson> getAcceptedLessonsByCourseId(Long courseId) {
        return lessonRepository.findAcceptedLessonsByCourseId(courseId);
    }

    @Override
    public List<Lesson> getPendingLessons() {
        return lessonRepository.findPendingLessons();
    }

    @Override
    public Lesson getLessonById(Long id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
    }

    @Override
    public Lesson getLessonByTitle(String title) {
        return lessonRepository.findByTitle(title)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
    }

    @Override
    public Lesson updateLesson(Lesson lesson) {
        return lessonRepository.save(lesson);
    }

    @Override
    public void deleteLesson(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        lessonRepository.delete(lesson);
    }

    @Override
    public List<Lesson> getLessonsByStatus(LessonStatus lessonStatus){
        return lessonRepository.findLessonByStatus(lessonStatus);
    }
}

