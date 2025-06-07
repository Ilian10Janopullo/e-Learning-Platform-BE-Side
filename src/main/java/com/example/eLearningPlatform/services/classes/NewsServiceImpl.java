package com.example.eLearningPlatform.services.classes;

import jakarta.persistence.criteria.Predicate;
import com.example.eLearningPlatform.models.entities.News;
import com.example.eLearningPlatform.models.enums.NewsCategory;
import com.example.eLearningPlatform.models.enums.NewsStatus;
import org.springframework.data.jpa.domain.Specification;
import com.example.eLearningPlatform.repositories.NewsRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.eLearningPlatform.services.interfaces.NewsService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;

    @Autowired
    public NewsServiceImpl(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @Override
    public News saveNews(News news) {
        return newsRepository.save(news);
    }

    @Override
    public Optional<News> getNewsById(Long id) {
        return newsRepository.findById(id);
    }

    @Override
    public List<News> getAllNews() {
        return newsRepository.findAll();
    }

    @Override
    public List<News> getNewsByAdminId(Long adminId) {
        return newsRepository.findByCreatedById(adminId);
    }

    @Override
    public List<News> getNewsByCategory(NewsCategory category) {
        return newsRepository.findByNewsCategory(category);
    }

    @Override
    public List<News> getNewsByStatus(NewsStatus status) {
        return newsRepository.findByNewsStatus(status);
    }

    @Override
    public List<News> searchNewsByTitle(String title) {
        return newsRepository.findByTitleContainingIgnoreCase(title);
    }

    @Override
    public void deleteNews(Long id) {
        newsRepository.deleteById(id);
    }

    @Override
    public List<News> getAllNews(Specification<News> specification) {
        return newsRepository.findAll(specification);
    }

    public static Specification<News> withFilters(NewsCategory category, String title) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("newsStatus"), NewsStatus.PUBLISHED));

            if (category != null) {
                predicates.add(cb.equal(root.get("newsCategory"), category));
            }

            if (title != null && !title.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<News> withFiltersForAdmin(NewsCategory category, String title, NewsStatus newsStatus) {

        if(newsStatus == null){
            return withFilters(category, title);
        }

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("newsStatus"), newsStatus));

            if (category != null) {
                predicates.add(cb.equal(root.get("newsCategory"), category));
            }

            if (title != null && !title.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }


}

