package com.example.eLearningPlatform.services.interfaces;

import com.example.eLearningPlatform.models.entities.News;
import com.example.eLearningPlatform.models.enums.NewsCategory;
import com.example.eLearningPlatform.models.enums.NewsStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface NewsService {
    News saveNews(News news);
    Optional<News> getNewsById(Long id);
    List<News> getAllNews();
    List<News> getNewsByAdminId(Long adminId);
    List<News> getNewsByCategory(NewsCategory category);
    List<News> getNewsByStatus(NewsStatus status);
    List<News> searchNewsByTitle(String title);
    void deleteNews(Long id);
    List<News> getAllNews(Specification<News> specification);
}

