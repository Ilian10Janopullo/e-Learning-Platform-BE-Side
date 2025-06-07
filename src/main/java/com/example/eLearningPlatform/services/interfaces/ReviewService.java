package com.example.eLearningPlatform.services.interfaces;

import com.example.eLearningPlatform.models.entities.Review;
import java.util.List;
import java.util.Optional;

public interface ReviewService {
    Review addReview(Long studentId, Long courseId, int rating, String comment);
    Review getReviewById(Long reviewId);
    void updateReview(Review review);
    Optional<Review> getReviewByStudentAndCourse(Long studentId, Long courseId);
    List<Review> getReviewsByCourse(Long courseId);
    List<Review> getReviewsByStudent(Long studentId);
    double getAverageRatingForCourse(Long courseId);
    void deleteReview(Long reviewId);
}
