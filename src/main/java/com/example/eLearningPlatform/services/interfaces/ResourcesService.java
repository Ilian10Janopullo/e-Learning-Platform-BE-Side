package com.example.eLearningPlatform.services.interfaces;

import com.example.eLearningPlatform.models.entities.Resources;
import java.util.List;
import java.util.Optional;

public interface ResourcesService {
    Resources addResource(Resources resources);
    List<Resources> getResourcesByLesson(Long lessonId);
    Resources getResourceById(Long resourceId);
    void deleteResource(Long resourceId);
    long countResourcesByLesson(Long lessonId);
    Optional<byte[]> getFileByResourceId(Long resourceId);
}
