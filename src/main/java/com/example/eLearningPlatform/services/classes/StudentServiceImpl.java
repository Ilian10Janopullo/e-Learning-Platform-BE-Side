package com.example.eLearningPlatform.services.classes;

import com.example.eLearningPlatform.config.services.JwtService;
import com.example.eLearningPlatform.utils.SaltUtil;
import jakarta.transaction.Transactional;
import com.example.eLearningPlatform.models.entities.Course;
import com.example.eLearningPlatform.models.entities.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.example.eLearningPlatform.repositories.CourseRepository;
import com.example.eLearningPlatform.repositories.StudentRepository;
import com.example.eLearningPlatform.services.interfaces.StudentService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class StudentServiceImpl implements StudentService {

    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository, CourseRepository courseRepository, JwtService jwtService, AuthenticationManager authManager) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.jwtService = jwtService;
        this.authManager = authManager;
    }

    @Override
    public Student createUser(Student student) {
        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        return studentRepository.save(student);
    }

    @Override
    public Optional<Student> getUserById(Long id) {
        return studentRepository.findById(id);
    }

    @Override
    public Optional<Student> getUserByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return studentRepository.existsByEmail(email);
    }

    @Override
    public List<Student> getStudentsByCourseId(Long courseId) {
        return studentRepository.findStudentsByCourseId(courseId);
    }

    @Override
    public List<Student> getStudentsWithCertificates() {
        return studentRepository.findStudentsWithCertificates();
    }

    @Override
    public void enrollStudentInCourse(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        student.getCourses().add(course);
        studentRepository.save(student);
    }

    @Override
    public Student updateUser(Student student) {
        return studentRepository.save(student);
    }

    @Override
    @Transactional
    public void updateProfilePicture(Long studentId, byte[] pictureData) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        student.setProfilePicture(pictureData);
        studentRepository.save(student);
    }

    @Override
    public String verify(String username, String password) {
        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(username, SaltUtil.getSaltedPassword(password)));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(username);
        } else {
            return "fail";
        }
    }

    @Override
    public void deleteUserById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        student.getCourses().clear();
        student.getProgressRecords().clear();
        student.getCertificates().clear();
        studentRepository.save(student);

        studentRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void addPurchasedCourses(Long studentId, Set<Course> courses) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        student.getCourses().addAll(courses);
        studentRepository.save(student);
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
}
