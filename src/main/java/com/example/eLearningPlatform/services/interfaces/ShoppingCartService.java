package com.example.eLearningPlatform.services.interfaces;

import com.example.eLearningPlatform.models.entities.ShoppingCart;
import com.example.eLearningPlatform.models.entities.Student;

import java.util.Optional;

public interface ShoppingCartService {
    ShoppingCart getCartByStudentId(Long studentId);
    void addCourseToCart(Long studentId, Long courseId);
    void removeCourseFromCart(Long studentId, Long courseId);
    void clearCart(Long studentId);
}

