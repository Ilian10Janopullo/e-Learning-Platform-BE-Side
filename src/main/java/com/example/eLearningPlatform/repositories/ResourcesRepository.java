package com.example.eLearningPlatform.repositories;

import com.example.eLearningPlatform.models.entities.Resources;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourcesRepository extends JpaRepository<Resources, Long> {

    List<Resources> findByLessonId(Long lessonId);
    long countByLessonId(Long lessonId);
}
