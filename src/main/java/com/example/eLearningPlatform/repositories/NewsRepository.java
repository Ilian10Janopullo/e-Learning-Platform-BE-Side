package com.example.eLearningPlatform.repositories;

import com.example.eLearningPlatform.models.entities.News;
import com.example.eLearningPlatform.models.enums.NewsCategory;
import com.example.eLearningPlatform.models.enums.NewsStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NewsRepository extends JpaRepository<News, Long>, JpaSpecificationExecutor<News> {
    List<News> findByCreatedById(Long adminId);
    List<News> findByTitleContainingIgnoreCase(String title);
    List<News> findTop5ByOrderByCreatedAtDesc();
    List<News> findByNewsCategory(NewsCategory category);
    List<News> findByNewsStatus(NewsStatus status);
    @Query("SELECT COUNT(n) FROM News n WHERE n.createdBy.id = :adminId")
    long countByAdminId(@Param("adminId") Long adminId);
    Optional<News> findTopByOrderByCreatedAtDesc();
}
