package com.example.eLearningPlatform.services.classes;

import com.example.eLearningPlatform.models.entities.Comment;
import com.example.eLearningPlatform.models.entities.Lesson;
import com.example.eLearningPlatform.models.entities.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.eLearningPlatform.repositories.CommentRepository;
import com.example.eLearningPlatform.repositories.LessonRepository;
import com.example.eLearningPlatform.repositories.StudentRepository;
import com.example.eLearningPlatform.services.interfaces.CommentService;

import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final LessonRepository lessonRepository;
    private final StudentRepository studentRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, LessonRepository lessonRepository, StudentRepository studentRepository) {
        this.commentRepository = commentRepository;
        this.lessonRepository = lessonRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public Comment addComment(Long studentId, Long lessonId, String content) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Comment comment = new Comment();

        comment.setLesson(lesson);
        comment.setStudent(student);
        comment.setContent(content);

        return commentRepository.save(comment);
    }

    @Override
    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
    }

    @Override
    public Comment replyComment(Long lessonId, Long studentId, String content, Long parentId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Comment comment = new Comment();

        comment.setLesson(lesson);
        comment.setStudent(student);
        comment.setContent(content);
        comment.setParentId(parentId);

        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getCommentsByLessonId(Long lessonId) {
        return commentRepository.findByLessonId(lessonId);
    }

    @Override
    public List<Comment> getCommentsByParentId(Long parentId) {
        return commentRepository.findByParentId(parentId);
    }

    @Override
    public long getRepliedNumberComments(Long parentId) {
        return commentRepository.countByParentId(parentId);
    }

    @Override
    public List<Comment> getCommentsByStudentId(Long studentId) {
        return commentRepository.findByStudentId(studentId);
    }

    @Override
    public Comment updateComment(Long commentId, String newContent) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        comment.setContent(newContent);
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        commentRepository.delete(comment);
    }

    @Override
    public long getNumberOfComments(Long lessonId) {
        return commentRepository.countByLessonId(lessonId);
    }

    @Override
    public Optional<Comment> getCommentByStudentAndLesson(Long studentId, Long lessonId) {
        return commentRepository.findByStudentIdAndLessonId(studentId, lessonId);
    }
}

