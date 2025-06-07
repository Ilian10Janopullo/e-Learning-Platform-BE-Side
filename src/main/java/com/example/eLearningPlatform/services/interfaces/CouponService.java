package com.example.eLearningPlatform.services.interfaces;

import com.example.eLearningPlatform.models.entities.Coupon;
import java.util.List;
import java.util.Optional;

public interface CouponService {

    Coupon create(Coupon coupon);
    Optional<Coupon> redeemByCodeAndCourseId(String code, Long courseId);
    List<Coupon> getCouponsByCourse(Long courseId);
    List<Coupon> getCouponsByStudent(Long studentId);
    List<Coupon> getCouponsByCourseAndStudent(Long courseId, Long studentId);
    List<Coupon> getCouponsByLecturer(Long lecturerId);
    Coupon getCouponsById(String couponId);
    boolean deleteCoupon(Coupon coupon);
    Coupon updateCoupon(Coupon coupon);
}
