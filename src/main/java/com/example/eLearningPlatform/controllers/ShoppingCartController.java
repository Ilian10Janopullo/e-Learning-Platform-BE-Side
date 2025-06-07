package com.example.eLearningPlatform.controllers;

import com.example.eLearningPlatform.controllers.dto.ApplyCouponDTO;
import com.example.eLearningPlatform.controllers.dto.respones.ShoppingCartResponseDTO;
import com.example.eLearningPlatform.models.entities.Coupon;
import com.example.eLearningPlatform.models.entities.Course;
import com.example.eLearningPlatform.models.entities.ShoppingCart;
import com.example.eLearningPlatform.models.entities.Student;
import com.example.eLearningPlatform.services.interfaces.CourseService;
import com.example.eLearningPlatform.utils.ResponseMessage;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.eLearningPlatform.services.interfaces.CouponService;
import com.example.eLearningPlatform.services.interfaces.ShoppingCartService;
import com.example.eLearningPlatform.services.interfaces.StudentService;
import com.example.eLearningPlatform.utils.SecurityUtil;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/shopping-cart")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;
    private final CouponService couponService;
    private final StudentService studentService;
    private final SecurityUtil securityUtil;
    private final CourseService courseService;

    @Autowired
    public ShoppingCartController(ShoppingCartService shoppingCartService, CouponService couponService, StudentService studentService, SecurityUtil securityUtil, CourseService courseService) {
        this.shoppingCartService = shoppingCartService;
        this.couponService = couponService;
        this.studentService = studentService;
        this.securityUtil = securityUtil;
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<ShoppingCartResponseDTO> getCart() {
        Long studentId = securityUtil.getAuthenticatedUserId();
        ShoppingCart cart = shoppingCartService.getCartByStudentId(studentId);
        ShoppingCartResponseDTO cartResponseDTO = new ShoppingCartResponseDTO();
        cartResponseDTO.setId(cart.getId());
        cartResponseDTO.setStudentId(cart.getStudent().getId());

        for(Course course : cart.getCourses()){

            if(!couponService.getCouponsByCourseAndStudent(course.getId(), studentId).isEmpty() && couponService.getCouponsByCourseAndStudent(course.getId(), studentId).getFirst().getExpirationDate().isAfter(LocalDateTime.now())){
                Coupon coupon = couponService.getCouponsByCourseAndStudent(course.getId(), studentId).getFirst();
                double discountPercentage = coupon.getDiscountPercentage();
                double discountAmount = course.getPrice() * (discountPercentage / 100.0);
                double newPrice = course.getPrice() - discountAmount;
                course.setPrice(newPrice);
            }

            cartResponseDTO.getCourseResponseDTOSet().add(CourseController.setCourseResponseDto(course));
        }

        return ResponseEntity.ok(cartResponseDTO);
    }

    @PostMapping("/add/{courseId}")
    public ResponseEntity<ResponseMessage> addCourse(@PathVariable Long courseId) {
        Long studentId = securityUtil.getAuthenticatedUserId();
        ShoppingCart cart = shoppingCartService.getCartByStudentId(studentId);

        Optional<Student> studentOpt = studentService.getUserById(studentId);

        if(studentOpt.isEmpty()){
            return ResponseEntity.badRequest().body(new ResponseMessage("Student is not found!"));
        }

        Student student = studentOpt.get();

        boolean alreadyBought = student.getCourses().stream()
                .anyMatch(course -> course.getId().equals(courseId));

        if (alreadyBought) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseMessage("Course is already in your library!"));
        }

        Course courseToBeBought = courseService.getCourseById(courseId);

        if(courseToBeBought.getPrice() == 0){
            studentService.addPurchasedCourses(studentId, Set.of(courseToBeBought));
            return ResponseEntity.ok(new ResponseMessage("Course is added to your library!."));
        }

        boolean alreadyAdded = cart.getCourses().stream()
                .anyMatch(course -> course.getId().equals(courseId));

        if (alreadyAdded) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseMessage("Course is already in your shopping cart."));
        }

        shoppingCartService.addCourseToCart(studentId, courseId);
        return ResponseEntity.ok(new ResponseMessage("Course added to shopping cart."));
    }


    @DeleteMapping("/remove/{courseId}")
    public ResponseEntity<ResponseMessage> removeCourse(@PathVariable Long courseId) {
        Long studentId = securityUtil.getAuthenticatedUserId();
        shoppingCartService.removeCourseFromCart(studentId, courseId);
        return ResponseEntity.ok(new ResponseMessage("Course removed from shopping cart."));
    }

    @PostMapping("/apply-coupon")
    public ResponseEntity<?> applyCouponToCart(@Valid @RequestBody ApplyCouponDTO applyCouponDTO) {

        Long studentId = securityUtil.getAuthenticatedUserId();

        ShoppingCart cart = shoppingCartService.getCartByStudentId(studentId);

        Course courseToBeBought = courseService.getCourseById(applyCouponDTO.getCourseId());

        Optional<Coupon> couponOpt = couponService.redeemByCodeAndCourseId(applyCouponDTO.getCouponCode(), applyCouponDTO.getCourseId());

        if (couponOpt.isEmpty()) {
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND, "Coupon not found!");
        }

        if(!shoppingCartService.getCartByStudentId(studentId).getCourses().contains(courseToBeBought)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Course is not in your library!");
        }

        Coupon coupon = couponOpt.get();
        if(!studentId.equals(coupon.getStudent().getId())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You are not allowed to apply coupon!");
        }

        shoppingCartService.removeCourseFromCart(studentId, courseToBeBought.getId());

        double discountPercentage = coupon.getDiscountPercentage();
        double discountAmount = courseToBeBought.getPrice() * (discountPercentage / 100.0);
        double newPrice = courseToBeBought.getPrice() - discountAmount;

        courseToBeBought.setPrice(newPrice);

        shoppingCartService.addCourseToCart(studentId, courseToBeBought.getId());

        return ResponseEntity.ok(new ResponseMessage("Coupon applied for this course!"));
    }

}
