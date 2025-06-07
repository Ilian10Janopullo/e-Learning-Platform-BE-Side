package com.example.eLearningPlatform.services.interfaces;

import com.example.eLearningPlatform.models.entities.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    Comment addComment(Long lessonId, Long studentId, String content);
    Comment getCommentById(Long commentId);
    List<Comment> getCommentsByLessonId(Long lessonId);
    List<Comment> getCommentsByParentId(Long parentId);
    long getRepliedNumberComments(Long parentId);
    Comment replyComment(Long lessonId, Long studentId, String content, Long parentId);
    List<Comment> getCommentsByStudentId(Long studentId);
    Comment updateComment(Long commentId, String newContent);
    void deleteComment(Long commentId);
    long getNumberOfComments(Long lessonId);
    Optional<Comment> getCommentByStudentAndLesson(Long studentId, Long lessonId);
}
