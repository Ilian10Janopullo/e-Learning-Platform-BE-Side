package com.example.eLearningPlatform.controllers;

import com.example.eLearningPlatform.models.entities.Coupon;
import com.example.eLearningPlatform.models.entities.Course;
import com.example.eLearningPlatform.models.entities.ShoppingCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.eLearningPlatform.services.interfaces.CouponService;
import com.example.eLearningPlatform.utils.SecurityUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/coupons")
public class StudentCouponController {

    private final CouponService couponService;
    private final SecurityUtil securityUtil;

    @Autowired
    public StudentCouponController(CouponService couponService, SecurityUtil securityUtil) {
        this.couponService = couponService;
        this.securityUtil = securityUtil;
    }

    @GetMapping("/my-coupons")
    public ResponseEntity<List<Coupon>> getMyCoupons() {
        Long studentId = securityUtil.getAuthenticatedUserId();
        List<Coupon> coupons = couponService.getCouponsByStudent(studentId);
        return ResponseEntity.ok(coupons);
    }

    @GetMapping("/lecturer/{couponId}") //get lecturer who created the coupon
    public ResponseEntity<String> getLecturer(@PathVariable String couponId) {

        Coupon coupons = couponService.getCouponsById(couponId);
        String lecturer_full_name = coupons.getLecturer().getFirstName() + " " + coupons.getLecturer().getLastName();
        return ResponseEntity.ok(lecturer_full_name);
    }

    @GetMapping("/course-name/{couponId}") //get the course name where you can apply the coupon
    public ResponseEntity<String> getCourse(@PathVariable String couponId) {

        Coupon coupons = couponService.getCouponsById(couponId);
        String course_title = coupons.getCourse().getName();
        return ResponseEntity.ok(course_title);
    }

}
