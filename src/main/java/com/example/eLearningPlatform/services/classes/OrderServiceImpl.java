package com.example.eLearningPlatform.services.classes;

import com.example.eLearningPlatform.models.entities.Order;
import com.example.eLearningPlatform.models.entities.ShoppingCart;
import com.example.eLearningPlatform.models.entities.Student;
import com.example.eLearningPlatform.models.entities.Course;
import com.example.eLearningPlatform.models.enums.OrderStatus;
import com.example.eLearningPlatform.services.interfaces.ShoppingCartService;
import com.example.eLearningPlatform.services.interfaces.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.eLearningPlatform.repositories.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.eLearningPlatform.services.interfaces.OrderService;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final StudentService studentService;
    private final ShoppingCartService shoppingCartService;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, StudentService studentService, ShoppingCartService shoppingCartService) {
        this.orderRepository = orderRepository;
        this.studentService = studentService;
        this.shoppingCartService = shoppingCartService;
    }

    @Override
    @Transactional
    public void createOrderFromCart(Long studentId, ShoppingCart cart, String sessionId) {
        double totalPrice = cart.getCourses().stream()
                .mapToDouble(Course::getPrice)
                .sum();

        Order order = new Order();
        Student student = studentService.getUserById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        order.setStudent(student);
        order.setSessionId(sessionId);
        order.setTotalPrice(totalPrice);
        order.setOrderDate(LocalDateTime.now());

        order.setCourses(new HashSet<>(cart.getCourses()));
        shoppingCartService.clearCart(studentId);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void confirmOrderFromCart(Long orderId){
        Optional<Order> orderOptional = orderRepository.findById(orderId);

        if (orderOptional.isEmpty()){
            throw new RuntimeException("Order could not be found!");
        }

        Order order = orderOptional.get();
        order.setOrderStatus(OrderStatus.PAID);


        studentService.addPurchasedCourses(order.getStudent().getId(), new HashSet<>(order.getCourses()));

        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId){
        Optional<Order> orderOptional = orderRepository.findById(orderId);

        if (orderOptional.isEmpty()){
            throw new RuntimeException("Order could not be found!");
        }

        Order order = orderOptional.get();
        order.setOrderStatus(OrderStatus.FAILED);

        orderRepository.save(order);
    }

    @Override
    public Optional<Order> getOrderBySessionId(String sessionId) {
        return orderRepository.findBySessionId(sessionId);
    }


    @Override
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> getOrdersByStudent(Student student) {
        return orderRepository.findByStudent(student);
    }

    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}


