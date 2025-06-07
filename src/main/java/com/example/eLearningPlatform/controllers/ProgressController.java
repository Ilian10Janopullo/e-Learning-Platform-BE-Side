package com.example.eLearningPlatform.controllers;

import com.example.eLearningPlatform.models.entities.Progress;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.eLearningPlatform.services.interfaces.ProgressService;
import com.example.eLearningPlatform.utils.SecurityUtil;


@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    private final ProgressService progressService;
    private final SecurityUtil securityUtil;

    @Autowired
    public ProgressController(ProgressService progressService, SecurityUtil securityUtil) {
        this.progressService = progressService;
        this.securityUtil = securityUtil;
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<Progress> getProgressForCourse(@PathVariable Long courseId) {
        Long studentId = securityUtil.getAuthenticatedUserId();

        if(progressService.getProgress(studentId, courseId) != null){
            Progress progress = progressService.getProgress(studentId, courseId);

            return ResponseEntity.ok(progress);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/finish/{courseId}/{lessonId}")
    public ResponseEntity<Progress> finishLesson(@PathVariable Long courseId,
                                                 @PathVariable Long lessonId) {

        Long studentId = securityUtil.getAuthenticatedUserId();

        Progress updatedProgress = progressService.updateProgress(studentId, courseId, lessonId);

        return ResponseEntity.ok(updatedProgress);
    }
}
