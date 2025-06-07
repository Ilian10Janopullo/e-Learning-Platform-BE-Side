package com.example.eLearningPlatform.utils;

import com.example.eLearningPlatform.config.entities.UserPrincipal;
import com.example.eLearningPlatform.repositories.AdminRepository;
import com.example.eLearningPlatform.repositories.LecturerRepository;
import com.example.eLearningPlatform.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    @Autowired
    public StudentRepository studentRepository;

    @Autowired
    public LecturerRepository lecturerRepository;

    @Autowired
    public AdminRepository adminRepository;

    public String getAuthenticatedUserUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            return ((UserPrincipal) authentication.getPrincipal()).getUsername();
        }
        throw new RuntimeException("User is not authenticated");
    }

    public Long getAuthenticatedUserId() {

        String username = getAuthenticatedUserUsername();

        if (adminRepository.findByUsername(username).isPresent()) {
            return adminRepository.findByUsername(username).get().getId();
        } else if (lecturerRepository.findByUsername(username).isPresent()) {
            return lecturerRepository.findByUsername(username).get().getId();
        } else {
            return studentRepository.findByUsername(username).get().getId();
        }
    }
}
