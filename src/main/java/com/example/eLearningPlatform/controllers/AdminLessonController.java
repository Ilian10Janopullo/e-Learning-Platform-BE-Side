package com.example.eLearningPlatform.controllers;

import com.example.eLearningPlatform.config.services.EmailService;
import com.example.eLearningPlatform.config.services.S3Service;
import com.example.eLearningPlatform.controllers.dto.respones.LessonResponseDTO;
import com.example.eLearningPlatform.models.entities.Admin;
import com.example.eLearningPlatform.models.entities.Comment;
import com.example.eLearningPlatform.models.entities.Lesson;
import com.example.eLearningPlatform.models.entities.Student;
import com.example.eLearningPlatform.models.enums.LessonStatus;
import com.example.eLearningPlatform.services.interfaces.AdminService;
import com.example.eLearningPlatform.services.interfaces.LessonService;
import com.example.eLearningPlatform.utils.EmailMessages;
import com.example.eLearningPlatform.utils.ResponseMessage;
import com.example.eLearningPlatform.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/lessons")
public class AdminLessonController {

    private final LessonService lessonService;
    private final S3Service s3Service;
    private final AdminService adminService;
    private final SecurityUtil securityUtil;
    private final EmailService emailService;

    @Autowired
    public AdminLessonController(LessonService lessonService, S3Service s3Service, AdminService adminService, SecurityUtil securityUtil, EmailService emailService) {
        this.lessonService = lessonService;
        this.s3Service = s3Service;
        this.adminService = adminService;
        this.securityUtil = securityUtil;

        this.emailService = emailService;
    }

    @GetMapping("/pending")
    public ResponseEntity<List<LessonResponseDTO>> getPendingLessons() throws IOException {

        Long userId = securityUtil.getAuthenticatedUserId();
        Optional<Admin> admin = adminService.getUserById(userId);

        if (admin.isEmpty()) {
            throw new RuntimeException("You are not an admin!");
        }

        List<Lesson> pendingLessons = lessonService.getLessonsByStatus(LessonStatus.PENDING);

        for (Lesson lesson : pendingLessons) {
            lesson.setVideo(s3Service.download(lesson.getVideo()));
        }

        List<LessonResponseDTO> lessonResponseDTOS = new ArrayList<>();

        for (Lesson lesson : pendingLessons) {
            LessonResponseDTO lessonResponseDTO = new LessonResponseDTO();
            lessonResponseDTO.setLessonId(lesson.getId());
            lessonResponseDTO.setTitle(lesson.getTitle());
            lessonResponseDTO.setDescription(lesson.getDescription());
            lessonResponseDTO.setVideo(lesson.getVideo());
            lessonResponseDTO.setResources(lesson.getResources());
            lessonResponseDTO.setCourseId(lesson.getCourse().getId());
            lessonResponseDTO.setStatus(lesson.getStatus());
            lessonResponseDTO.setCreatedAt(lesson.getCreatedAt());

            for(Comment comment : lesson.getComments()) {
                lessonResponseDTO.getComments().add(FeedbackController.convertToCommentsResponseDTO(comment));
            }

            lessonResponseDTOS.add(lessonResponseDTO);
        }

        return ResponseEntity.ok(lessonResponseDTOS);
    }

    @PutMapping("/{lessonId}/approve")
    public ResponseEntity<Lesson> approveLesson(@PathVariable Long lessonId) {

        Long userId = securityUtil.getAuthenticatedUserId();
        Optional<Admin> admin = adminService.getUserById(userId);

        if (admin.isEmpty()) {
            throw new RuntimeException("You are not an admin!");
        }

        Lesson lesson = lessonService.getLessonById(lessonId);

        lesson.setStatus(LessonStatus.ACCEPTED);
        Lesson updatedLesson = lessonService.updateLesson(lesson);

        for(Student student : updatedLesson.getCourse().getStudents()){
            emailService.sendSimpleEmail(student.getEmail(), "Time to get back to eLearning!", EmailMessages.approveMaterialMessage(updatedLesson, student));
        }

        return ResponseEntity.ok(updatedLesson);
    }


    @DeleteMapping("/delete/{lessonId}")
    public ResponseEntity<ResponseMessage> deleteLesson(@PathVariable Long lessonId) {

        Long userId = securityUtil.getAuthenticatedUserId();
        Optional<Admin> admin = adminService.getUserById(userId);

        if (admin.isEmpty()) {
            throw new RuntimeException("You are not an admin!");
        }

        Lesson lesson = lessonService.getLessonById(lessonId);

        s3Service.delete(lesson.getVideo());
        lessonService.deleteLesson(lessonId);
        return ResponseEntity.ok(new ResponseMessage("Lesson deleted successfully."));
    }
}
