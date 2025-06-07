package com.example.eLearningPlatform.controllers;

import com.example.eLearningPlatform.controllers.dto.CommentDTO;
import com.example.eLearningPlatform.controllers.dto.ReviewDTO;
import com.example.eLearningPlatform.controllers.dto.respones.CommentsResponseDTO;
import com.example.eLearningPlatform.controllers.dto.respones.ReviewResponseDTO;
import com.example.eLearningPlatform.models.entities.Comment;
import com.example.eLearningPlatform.models.entities.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.eLearningPlatform.services.interfaces.CommentService;
import com.example.eLearningPlatform.services.interfaces.ReviewService;
import com.example.eLearningPlatform.utils.SecurityUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final CommentService commentService;
    private final ReviewService reviewService;
    private final SecurityUtil securityUtil;

    @Autowired
    public FeedbackController(CommentService commentService, ReviewService reviewService, SecurityUtil securityUtil) {
        this.commentService = commentService;
        this.reviewService = reviewService;
        this.securityUtil = securityUtil;
    }

    @PostMapping("/lessons/{lessonId}/add-comment")
    public ResponseEntity<CommentsResponseDTO> postComment(@PathVariable Long lessonId,
                                               @RequestBody CommentDTO commentDto) {
        Long studentId = securityUtil.getAuthenticatedUserId();

        Optional<Comment> existingComment = commentService.getCommentByStudentAndLesson(studentId, lessonId);

        if (existingComment.isPresent()) {
            Comment comment = existingComment.get();
            comment = commentService.updateComment(comment.getId(), commentDto.getContent());
            return ResponseEntity.ok(convertToCommentsResponseDTO(comment));
        }

        Comment comment = commentService.addComment(studentId, lessonId, commentDto.getContent());
        return ResponseEntity.ok(convertToCommentsResponseDTO(comment));
    }

    @PostMapping("/lessons/{lessonId}/reply-comment")
    public ResponseEntity<Comment> postReplyComment(@PathVariable Long lessonId,
                                               @RequestBody CommentDTO commentDto) {
        Long studentId = securityUtil.getAuthenticatedUserId();
        Comment comment = commentService.replyComment(studentId, lessonId, commentDto.getContent(), commentDto.getParentId());
        return ResponseEntity.ok(comment);
    }

    @PostMapping("/courses/{courseId}/add-review")
    public ResponseEntity<ReviewResponseDTO> postReview(@PathVariable Long courseId,
                                             @RequestBody ReviewDTO reviewDto) {
        Long studentId = securityUtil.getAuthenticatedUserId();

        Optional<Review> existingReview = reviewService.getReviewByStudentAndCourse(studentId, courseId);

        if(existingReview.isPresent()) {
            Review review = existingReview.get();
            review.setRating(reviewDto.getRating());
            review.setComment(reviewDto.getReview());
            reviewService.updateReview(review);
            return ResponseEntity.ok(setReviewResponseDto(review));
        }

        Review review = reviewService.addReview(studentId, courseId, reviewDto.getRating(), reviewDto.getReview());
        return ResponseEntity.ok(setReviewResponseDto(review));
    }

    @GetMapping("/lessons/{lessonId}/get-comments")
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long lessonId) {
        List<Comment> comment = commentService.getCommentsByLessonId(lessonId);
        return ResponseEntity.ok(comment);
    }

    @GetMapping("/get-comments/{parentId}")
    public ResponseEntity<List<CommentsResponseDTO>> getRepliedComments(@PathVariable Long parentId) {
        List<Comment> comment = commentService.getCommentsByParentId(parentId);
        List<CommentsResponseDTO> commentsResponseDTOS = new ArrayList<>();

        for(Comment comment1 : comment) {
            commentsResponseDTOS.add(convertToCommentsResponseDTO(comment1));
        }

        return ResponseEntity.ok(commentsResponseDTOS);
    }

    @GetMapping("/courses/{courseId}/get-reviews")
    public ResponseEntity<List<ReviewResponseDTO>> getReviews(@PathVariable Long courseId) {

        List<Review> reviews = reviewService.getReviewsByCourse(courseId);
        List<ReviewResponseDTO> reviewResponseDTOS = new ArrayList<>();

        for(Review review : reviews) {
            reviewResponseDTOS.add(setReviewResponseDto(review));
        }

        return ResponseEntity.ok(reviewResponseDTOS);
    }

    @GetMapping("/courses/{courseId}/get-reviews/rating")
    public ResponseEntity<Double> getCourseRating(@PathVariable Long courseId) {
        Double reviewNumber = reviewService.getAverageRatingForCourse(courseId);
        return ResponseEntity.ok(reviewNumber);
    }

    @GetMapping("/courses/{courseId}/get-reviews/count")
    public ResponseEntity<Integer> getReviewsCount(@PathVariable Long courseId) {
        Integer reviewNumber = reviewService.getReviewsByCourse(courseId).size();
        return ResponseEntity.ok(reviewNumber);
    }

    @GetMapping("/lessons/{lessonId}/get-comments/count")
    public ResponseEntity<Long> getCommentsCount(@PathVariable Long lessonId) {
        Long commentNumber = commentService.getNumberOfComments(lessonId);
        return ResponseEntity.ok(commentNumber);
    }

    @GetMapping("/get-replied-comments/count/{parentId}")
    public ResponseEntity<Long> getRepliedCommentsCount(@PathVariable Long parentId) {
        Long commentNumber = commentService.getRepliedNumberComments(parentId);
        return ResponseEntity.ok(commentNumber);
    }

    static CommentsResponseDTO convertToCommentsResponseDTO(Comment comment) {
        CommentsResponseDTO responseDTO = new CommentsResponseDTO();
        responseDTO.setId(comment.getId());
        responseDTO.setContent(comment.getContent());
        responseDTO.setUpdatedAt(comment.getLastUpdatedAt());
        responseDTO.setParentId(comment.getParentId());
        responseDTO.setUserName(comment.getStudent().getUsername());
        responseDTO.setLessonId(comment.getLesson().getId());
        return responseDTO;
    }

    static ReviewResponseDTO setReviewResponseDto(Review review){
        ReviewResponseDTO reviewResponseDTO = new ReviewResponseDTO();
        reviewResponseDTO.setId(review.getId());
        reviewResponseDTO.setContent(review.getComment());
        reviewResponseDTO.setRating(review.getRating());
        reviewResponseDTO.setStudentUsername(review.getStudent().getUsername());
        reviewResponseDTO.setLastUpdatedAt(review.getLastUpdatedAt());
        reviewResponseDTO.setCourseId(review.getCourse().getId());
        return reviewResponseDTO;
    }

}
