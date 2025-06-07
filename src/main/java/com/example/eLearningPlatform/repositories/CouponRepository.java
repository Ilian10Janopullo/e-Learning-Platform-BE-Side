package com.example.eLearningPlatform.repositories;

import com.example.eLearningPlatform.models.entities.Coupon;
import com.example.eLearningPlatform.models.entities.Course;
import com.example.eLearningPlatform.models.entities.Lecturer;
import com.example.eLearningPlatform.models.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, String> {

    List<Coupon> findByCourseAndExpirationDateAfter(Optional<Course> course, LocalDateTime now);

    List<Coupon> findByStudentAndExpirationDateAfterOrderByExpirationDateAsc(Optional<Student> student, LocalDateTime now);

    List<Coupon> findByLecturerAndExpirationDateAfter(Optional<Lecturer> lecturer, LocalDateTime now);

    Optional<Coupon> findByCouponCodeAndExpirationDateAfterAndCourse(String couponCode, LocalDateTime now, Optional<Course> course);

    List<Coupon> findByCourseAndStudentAndExpirationDateAfter(Optional<Course> course, Optional<Student> student, LocalDateTime now);
}
