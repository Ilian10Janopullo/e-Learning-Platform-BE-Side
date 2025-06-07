package com.example.eLearningPlatform.repositories;

import com.example.eLearningPlatform.models.entities.ShoppingCart;
import com.example.eLearningPlatform.models.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    ShoppingCart findByStudentId(Long studentId);
}
