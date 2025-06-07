package com.example.eLearningPlatform.services.interfaces;

import com.example.eLearningPlatform.models.entities.Progress;
import java.util.List;
import java.util.Optional;

public interface ProgressService {
    Progress updateProgress(Long studentId, Long courseId, Long lessonId);
    Progress getProgress(Long studentId, Long courseId);
    List<Progress> getStudentProgress(Long studentId);
    List<Progress> getCourseProgress(Long courseId);
    long countCompletedStudents(Long courseId);
    double getAverageProgress(Long courseId);

    List<Progress> findIncompleteEnrollments();
}
