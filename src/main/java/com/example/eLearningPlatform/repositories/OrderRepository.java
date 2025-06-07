package com.example.eLearningPlatform.repositories;

import com.example.eLearningPlatform.models.entities.Order;
import com.example.eLearningPlatform.models.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStudent(Student student);
    Optional<Order> findBySessionId(String sessionId);
}
