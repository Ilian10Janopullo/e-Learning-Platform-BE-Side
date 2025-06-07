package com.example.eLearningPlatform.services.classes;

import com.example.eLearningPlatform.models.entities.Review;
import com.example.eLearningPlatform.models.entities.Student;
import com.example.eLearningPlatform.models.entities.Course;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.eLearningPlatform.repositories.ReviewRepository;
import com.example.eLearningPlatform.repositories.StudentRepository;
import com.example.eLearningPlatform.repositories.CourseRepository;
import com.example.eLearningPlatform.services.interfaces.ReviewService;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             StudentRepository studentRepository,
                             CourseRepository courseRepository) {
        this.reviewRepository = reviewRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    @Transactional
    public Review addReview(Long studentId, Long courseId, int rating, String comment) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Review review = new Review();
        review.setStudent(student);
        review.setCourse(course);
        review.setRating(rating);
        review.setComment(comment);

        return reviewRepository.save(review);
    }

    @Override
    public Review getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
    }

    @Override
    public void updateReview(Review review) {
        if (!reviewRepository.existsById(review.getId())) {
            throw new RuntimeException("Review not found");
        }
        reviewRepository.save(review);
    }

    @Override
    public Optional<Review> getReviewByStudentAndCourse(Long studentId, Long courseId) {
        return reviewRepository.findByStudentIdAndCourseId(studentId, courseId);
    }

    @Override
    public List<Review> getReviewsByCourse(Long courseId) {
        return reviewRepository.findByCourseId(courseId);
    }

    @Override
    public List<Review> getReviewsByStudent(Long studentId) {
        return reviewRepository.findByStudentId(studentId);
    }

    @Override
    public double getAverageRatingForCourse(Long courseId) {
        List<Review> reviews = reviewRepository.findByCourseId(courseId);
        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new RuntimeException("Review not found");
        }
        reviewRepository.deleteById(reviewId);
    }
}
