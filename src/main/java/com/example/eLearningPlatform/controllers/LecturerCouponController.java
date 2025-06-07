package com.example.eLearningPlatform.controllers;

import com.example.eLearningPlatform.config.services.EmailService;
import com.example.eLearningPlatform.controllers.dto.CouponCreationDTO;
import com.example.eLearningPlatform.controllers.dto.CouponUpdateDTO;
import com.example.eLearningPlatform.utils.EmailMessages;
import com.example.eLearningPlatform.utils.ResponseMessage;
import jakarta.validation.Valid;
import com.example.eLearningPlatform.models.entities.Course;
import com.example.eLearningPlatform.models.entities.Coupon;
import com.example.eLearningPlatform.models.entities.Lecturer;
import com.example.eLearningPlatform.models.entities.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.eLearningPlatform.services.interfaces.CourseService;
import com.example.eLearningPlatform.services.interfaces.CouponService;
import com.example.eLearningPlatform.services.interfaces.LecturerService;
import com.example.eLearningPlatform.services.interfaces.StudentService;
import com.example.eLearningPlatform.utils.SecurityUtil;
import java.util.List;

@RestController
@RequestMapping("/api/lecturer/courses/{courseId}/coupons")
public class LecturerCouponController {

    private final CourseService courseService;
    private final CouponService couponService;
    private final LecturerService lecturerService;
    private final StudentService studentService;
    private final SecurityUtil securityUtil;
    private final EmailService emailService;

    @Autowired
    public LecturerCouponController(CourseService courseService,
                                    CouponService couponService,
                                    LecturerService lecturerService,
                                    StudentService studentService, SecurityUtil securityUtil, EmailService emailService) {
        this.courseService = courseService;
        this.couponService = couponService;
        this.lecturerService = lecturerService;
        this.studentService = studentService;
        this.securityUtil = securityUtil;
        this.emailService = emailService;
    }

    @PostMapping("/create-coupon")
    public ResponseEntity<ResponseMessage> createCoupon(@PathVariable Long courseId, @RequestBody @Valid CouponCreationDTO couponCreationDTO){

        Long lecturerId = securityUtil.getAuthenticatedUserId();

        Course course = courseService.getCourseById(courseId);

        if (!course.getLecturer().getId().equals(lecturerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(null);
        }

        Lecturer lecturer = lecturerService.getUserById(lecturerId)
                .orElseThrow(() -> new RuntimeException("Lecturer not found"));

        Coupon coupon = new Coupon();
        coupon.setDiscountPercentage(couponCreationDTO.getDiscountPercentage());
        coupon.setExpirationDate(couponCreationDTO.getExpirationDate());
        coupon.setCourse(course);
        coupon.setLecturer(lecturer);

        if(couponCreationDTO.getStudentEmail() != null){
            Student student = studentService.getUserByEmail(couponCreationDTO.getStudentEmail()).orElseThrow(() -> new RuntimeException("Student not found"));
            coupon.setStudent(student);
        }

        Coupon createdCoupon = couponService.create(coupon);
        emailService.sendSimpleEmail(createdCoupon.getStudent().getEmail(), "Claim Your Coupon Before It Expires!", EmailMessages.couponNotificationMessage(createdCoupon));
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage("Coupon Created Successfully " + createdCoupon.getCouponCode()));

    }

    @GetMapping
    public ResponseEntity<List<Coupon>> listCouponsOfACourse(@PathVariable Long courseId){

        Long lecturerId = securityUtil.getAuthenticatedUserId();

        Course course = courseService.getCourseById(courseId);

        if (!course.getLecturer().getId().equals(lecturerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(null);
        }

        List<Coupon> coupons = couponService.getCouponsByCourse(courseId);

        return ResponseEntity.ok(coupons);
    }

    @PutMapping("/update-coupon/{couponId}")
    public ResponseEntity<Coupon> updateCoupon(@PathVariable Long courseId,
                                               @PathVariable String couponId,
                                               @RequestBody @Valid CouponUpdateDTO couponUpdateDTO) {
        Long lecturerId = securityUtil.getAuthenticatedUserId();
        Course course = courseService.getCourseById(courseId);


        if (!course.getLecturer().getId().equals(lecturerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Coupon coupon = couponService.getCouponsById(couponId);

        coupon.setDiscountPercentage(couponUpdateDTO.getDiscountPercentage());
        coupon.setExpirationDate(couponUpdateDTO.getExpirationDate());

        if(couponUpdateDTO.getStudentEmail() != null){
            Student student = studentService.getUserByEmail(couponUpdateDTO.getStudentEmail()).orElseThrow(() -> new RuntimeException("Student not found"));
            coupon.setStudent(student);
        }

        Coupon updatedCoupon = couponService.updateCoupon(coupon);
        return ResponseEntity.ok(updatedCoupon);
    }


    @DeleteMapping("/delete-coupon/{couponId}")
    public ResponseEntity<ResponseMessage> deleteCoupon(@PathVariable Long courseId,
                                                        @PathVariable String couponId) {
        Long lecturerId = securityUtil.getAuthenticatedUserId();
        Course course = courseService.getCourseById(courseId);
        if (!course.getLecturer().getId().equals(lecturerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseMessage("You are not authorized to delete this coupon."));
        }

        Coupon coupon = couponService.getCouponsById(couponId);

        couponService.deleteCoupon(coupon);
        return ResponseEntity.ok(new ResponseMessage("Coupon deleted successfully."));
    }
}
