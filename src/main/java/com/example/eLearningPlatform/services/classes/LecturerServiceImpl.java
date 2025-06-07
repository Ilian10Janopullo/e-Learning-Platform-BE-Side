package com.example.eLearningPlatform.services.classes;

import com.example.eLearningPlatform.config.services.JwtService;
import com.example.eLearningPlatform.models.entities.Admin;
import com.example.eLearningPlatform.utils.SaltUtil;
import jakarta.transaction.Transactional;
import com.example.eLearningPlatform.models.entities.Lecturer;
import com.example.eLearningPlatform.models.entities.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.example.eLearningPlatform.repositories.LecturerRepository;
import com.example.eLearningPlatform.services.interfaces.LecturerService;

import java.util.List;
import java.util.Optional;

@Service
public class LecturerServiceImpl implements LecturerService {

    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final LecturerRepository lecturerRepository;

    @Autowired
    public LecturerServiceImpl(LecturerRepository lecturerRepository, JwtService jwtService, AuthenticationManager authManager) {
        this.lecturerRepository = lecturerRepository;
        this.jwtService = jwtService;
        this.authManager = authManager;
    }

    @Override
    public Lecturer createUser(Lecturer lecturer) {
        if (lecturerRepository.existsByEmail(lecturer.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        return lecturerRepository.save(lecturer);
    }

    @Override
    public Optional<Lecturer> getUserById(Long id) {
        return lecturerRepository.findById(id);
    }

    @Override
    public Optional<Lecturer> getUserByEmail(String email) {
        return lecturerRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return lecturerRepository.existsByEmail(email);
    }

    @Override
    public Optional<Lecturer> getLecturerByCourseId(Long courseId) {
        return lecturerRepository.findLecturersByCourseId(courseId);
    }

    @Override
    public long countCoursesByLecturerId(Long lecturerId) {
        return lecturerRepository.countCoursesByLecturerId(lecturerId);
    }

    @Override
    public List<Lecturer> getAllLecturers() {
        return lecturerRepository.findAll();
    }

    @Override
    public Lecturer updateUser (Lecturer lecturer) {
        return lecturerRepository.save(lecturer);
    }

    @Override
    @Transactional
    public void updateProfilePicture(Long lecturerId, byte[] pictureData) {
        Lecturer lecturer = lecturerRepository.findById(lecturerId)
                .orElseThrow(() -> new RuntimeException("Lecturer not found"));
        lecturer.setProfilePicture(pictureData);
        lecturerRepository.save(lecturer);
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
        Lecturer lecturer = lecturerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lecturer not found"));

//        if (!lecturer.getCourses().isEmpty()) {
//            throw new RuntimeException("Cannot delete lecturer with active courses.");
//        }
//        taken care of the active courses even if you delete the lecturers

        lecturerRepository.deleteById(id);
    }
}

