package com.example.eLearningPlatform.controllers;

import com.example.eLearningPlatform.controllers.dto.FlagDTO;
import com.example.eLearningPlatform.controllers.dto.respones.FlagResponseDTO;
import com.example.eLearningPlatform.models.entities.Admin;
import com.example.eLearningPlatform.models.entities.Flag;
import com.example.eLearningPlatform.models.enums.FlaggedType;
import com.example.eLearningPlatform.services.interfaces.AdminService;
import com.example.eLearningPlatform.services.interfaces.CommentService;
import com.example.eLearningPlatform.services.interfaces.ReviewService;
import com.example.eLearningPlatform.utils.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.eLearningPlatform.services.interfaces.FlagService;
import com.example.eLearningPlatform.utils.SecurityUtil;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/flags")
public class FlagController {

    private final FlagService flagService;
    private final SecurityUtil securityUtil;
    private final AdminService adminService;
    private final CommentService commentService;
    private final ReviewService reviewService;

    @Autowired
    public FlagController(FlagService flagService, SecurityUtil securityUtil, AdminService adminService, CommentService commentService, ReviewService reviewService) {
        this.flagService = flagService;
        this.securityUtil = securityUtil;
        this.adminService = adminService;
        this.commentService = commentService;
        this.reviewService = reviewService;
    }

    // Endpoint to raise a flag on a comment or review
    @PostMapping("/raise-flag")
    public ResponseEntity<Flag> raiseFlag(@RequestBody @Valid FlagDTO flagDto) {
        Long userId = securityUtil.getAuthenticatedUserId();

        if(flagService.getFlagByObjectIdAndContentTypeAndUserId(flagDto.getObjectId(), flagDto.getContentType(), userId).isPresent()) {
            return ResponseEntity.badRequest().body(null);
        }

        Flag flag = flagService.raiseFlag(userId, flagDto);
        return ResponseEntity.ok(flag);
    }

    @GetMapping
    public ResponseEntity<List<FlagResponseDTO>> getAllFlags() {

        Long userId = securityUtil.getAuthenticatedUserId();
        Optional<Admin> admin = adminService.getUserById(userId);

        if (admin.isEmpty()) {
            throw new RuntimeException("You are not an admin!");
        }

        List<Flag> flags = flagService.getAllFlags();
        List<FlagResponseDTO> flagResponseDTOS = new ArrayList<>();

        for(Flag flag : flags) {

            FlagResponseDTO flagResponseDTO = new FlagResponseDTO();
            flagResponseDTO.setId(flag.getId());
            flagResponseDTO.setFlaggedType(flag.getContentType());
            flagResponseDTO.setReason(flag.getReason());


            if (flag.getContentType() == FlaggedType.COMMENT) {
                flag.setReason(commentService.getCommentById(flag.getObjectId()).getContent());
                flagResponseDTO.setContent(commentService.getCommentById(flag.getObjectId()).getContent());
                flagResponseDTO.setCourseName(commentService.getCommentById(flag.getObjectId()).getLesson().getCourse().getName());
                flagResponseDTO.setLessonName(commentService.getCommentById(flag.getObjectId()).getLesson().getTitle());
            } else if (flag.getContentType() == FlaggedType.REVIEW) {
                flag.setReason(reviewService.getReviewById(flag.getObjectId()).getComment());
                flagResponseDTO.setContent(reviewService.getReviewById(flag.getObjectId()).getComment());
                flagResponseDTO.setCourseName(reviewService.getReviewById(flag.getObjectId()).getCourse().getName());
                flagResponseDTO.setLessonName("N/A");
            }

            flagResponseDTOS.add(flagResponseDTO);

        }

        return ResponseEntity.ok(flagResponseDTOS);
    }

    @GetMapping("/comments")
    public ResponseEntity<List<FlagResponseDTO>> getCommentFlags() {

        Long userId = securityUtil.getAuthenticatedUserId();
        Optional<Admin> admin = adminService.getUserById(userId);

        if (admin.isEmpty()) {
            throw new RuntimeException("You are not an admin!");
        }

        List<Flag> commentFlags = flagService.getFlagByContentType(FlaggedType.COMMENT);
        List<FlagResponseDTO> flagResponseDTOS = new ArrayList<>();

        for(Flag flag : commentFlags) {
            FlagResponseDTO flagResponseDTO = new FlagResponseDTO();
            flagResponseDTO.setId(flag.getId());
            flagResponseDTO.setFlaggedType(flag.getContentType());
            flagResponseDTO.setReason(flag.getReason());
            flagResponseDTO.setContent(commentService.getCommentById(flag.getObjectId()).getContent());
            flagResponseDTO.setCourseName(commentService.getCommentById(flag.getObjectId()).getLesson().getCourse().getName());
            flagResponseDTO.setLessonName(commentService.getCommentById(flag.getObjectId()).getLesson().getTitle());
            flagResponseDTOS.add(flagResponseDTO);
        }

        return ResponseEntity.ok(flagResponseDTOS);
    }

    @GetMapping("/reviews")
    public ResponseEntity<List<FlagResponseDTO>> getReviewFlags() {

        Long userId = securityUtil.getAuthenticatedUserId();
        Optional<Admin> admin = adminService.getUserById(userId);

        if (admin.isEmpty()) {
            throw new RuntimeException("You are not an admin!");
        }

        List<Flag> reviewFlags = flagService.getFlagByContentType(FlaggedType.REVIEW);
        List<FlagResponseDTO> flagResponseDTOS = new ArrayList<>();

        for(Flag flag : reviewFlags) {
            FlagResponseDTO flagResponseDTO = new FlagResponseDTO();
            flagResponseDTO.setId(flag.getId());
            flagResponseDTO.setFlaggedType(flag.getContentType());
            flagResponseDTO.setReason(flag.getReason());
            flagResponseDTO.setContent(reviewService.getReviewById(flag.getObjectId()).getComment());
            flagResponseDTO.setCourseName(reviewService.getReviewById(flag.getObjectId()).getCourse().getName());
            flagResponseDTO.setLessonName("N/A");
            flagResponseDTOS.add(flagResponseDTO);
        }

        return ResponseEntity.ok(flagResponseDTOS);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessage> deleteFlag(@PathVariable Long id) {

        Long userId = securityUtil.getAuthenticatedUserId();
        Optional<Admin> admin = adminService.getUserById(userId);

        if (admin.isEmpty()) {
            throw new RuntimeException("You are not an admin!");
        }

        flagService.deleteFlag(id);
        return ResponseEntity.ok(new ResponseMessage("Flag deleted successfully."));
    }

    @DeleteMapping("/flagged-content/{id}")
    public ResponseEntity<ResponseMessage> deleteFlaggedContent(@PathVariable Long id) {

        Long userId = securityUtil.getAuthenticatedUserId();
        Optional<Admin> admin = adminService.getUserById(userId);

        if (admin.isEmpty()) {
            throw new RuntimeException("You are not an admin!");
        }

        Flag flag = flagService.getFlagById(id);

        flagService.deleteFlaggedContent(flag.getContentType(), flag.getObjectId());
        return ResponseEntity.ok(new ResponseMessage("Content deleted successfully."));
    }

}
