package com.example.eLearningPlatform.services.classes;

import com.example.eLearningPlatform.models.entities.ShoppingCart;
import com.example.eLearningPlatform.models.entities.Student;
import com.example.eLearningPlatform.models.entities.Course;
import com.example.eLearningPlatform.repositories.ShoppingCartRepository;
import com.example.eLearningPlatform.repositories.StudentRepository;
import com.example.eLearningPlatform.repositories.CourseRepository;
import com.example.eLearningPlatform.services.interfaces.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public ShoppingCartServiceImpl(ShoppingCartRepository shoppingCartRepository,
                                   StudentRepository studentRepository,
                                   CourseRepository courseRepository) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public ShoppingCart getCartByStudentId(Long studentId) {
        ShoppingCart cart = shoppingCartRepository.findByStudentId(studentId);
        if (cart == null) {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            cart = new ShoppingCart();
            cart.setStudent(student);
            cart = shoppingCartRepository.save(cart);
        }
        return cart;
    }

    @Override
    @Transactional
    public void addCourseToCart(Long studentId, Long courseId) {
        ShoppingCart cart = getCartByStudentId(studentId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        cart.getCourses().add(course);
        shoppingCartRepository.save(cart);
    }

    @Override
    @Transactional
    public void removeCourseFromCart(Long studentId, Long courseId) {
        ShoppingCart cart = getCartByStudentId(studentId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        cart.getCourses().remove(course);
        shoppingCartRepository.save(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long studentId) {
        ShoppingCart cart = getCartByStudentId(studentId);
        cart.getCourses().clear();
        shoppingCartRepository.save(cart);
    }
}
