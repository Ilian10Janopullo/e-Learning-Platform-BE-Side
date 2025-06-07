package com.example.eLearningPlatform.repositories;

import com.example.eLearningPlatform.models.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByLessonId(Long lessonId);
    List<Comment> findByStudentId(Long studentId);
    List<Comment> findByParentId(Long parentId);
    long countByParentId(Long parentId);
    long countByLessonId(Long lessonId);
    Optional<Comment> findByStudentIdAndLessonId(Long studentId, Long lessonId);
}
