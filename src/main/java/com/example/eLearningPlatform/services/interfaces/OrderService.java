package com.example.eLearningPlatform.services.interfaces;

import com.example.eLearningPlatform.models.entities.Order;
import com.example.eLearningPlatform.models.entities.ShoppingCart;
import com.example.eLearningPlatform.models.entities.Student;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    void createOrderFromCart(Long studentId, ShoppingCart cart, String sessionId);

    void confirmOrderFromCart(Long orderId);

    void cancelOrder(Long orderId);

    Optional<Order> getOrderBySessionId(String sessionId);

    Optional<Order> getOrderById(Long id);

    List<Order> getOrdersByStudent(Student student);

    void deleteOrder(Long id);
}
