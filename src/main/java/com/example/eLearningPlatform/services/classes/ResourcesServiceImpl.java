package com.example.eLearningPlatform.services.classes;

import com.example.eLearningPlatform.models.entities.Resources;
import com.example.eLearningPlatform.models.entities.Lesson;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.eLearningPlatform.repositories.ResourcesRepository;
import com.example.eLearningPlatform.repositories.LessonRepository;
import com.example.eLearningPlatform.services.interfaces.ResourcesService;

import java.util.List;
import java.util.Optional;

@Service
public class ResourcesServiceImpl implements ResourcesService {

    private final ResourcesRepository resourcesRepository;
    private final LessonRepository lessonRepository;

    public ResourcesServiceImpl(ResourcesRepository resourcesRepository, LessonRepository lessonRepository) {
        this.resourcesRepository = resourcesRepository;
        this.lessonRepository = lessonRepository;
    }

    @Override
    @Transactional
    public Resources addResource(Resources resource) {
        return resourcesRepository.save(resource);
    }

    @Override
    public List<Resources> getResourcesByLesson(Long lessonId) {
        return resourcesRepository.findByLessonId(lessonId);
    }

    @Override
    public Resources getResourceById(Long resourceId) {
        return resourcesRepository.findById(resourceId).orElseThrow(() -> new RuntimeException("Resource not found!"));
    }

    @Override
    public long countResourcesByLesson(Long lessonId) {
        return resourcesRepository.countByLessonId(lessonId);
    }

    @Override
    @Transactional
    public void deleteResource(Long resourceId) {
        if (!resourcesRepository.existsById(resourceId)) {
            throw new RuntimeException("Resource not found");
        }
        resourcesRepository.deleteById(resourceId);
    }

    @Override
    public Optional<byte[]> getFileByResourceId(Long resourceId) {
        return resourcesRepository.findById(resourceId).map(Resources::getFile);
    }
}
