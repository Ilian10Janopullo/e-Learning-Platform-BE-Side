package com.example.eLearningPlatform.services.classes;

import com.example.eLearningPlatform.config.services.JwtService;
import com.example.eLearningPlatform.utils.SaltUtil;
import jakarta.transaction.Transactional;
import com.example.eLearningPlatform.models.entities.Admin;
import com.example.eLearningPlatform.repositories.AdminRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.eLearningPlatform.services.interfaces.AdminService;
import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {

    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    private final AdminRepository adminRepository;

    @Autowired
    public AdminServiceImpl(AdminRepository adminRepository, JwtService jwtService, AuthenticationManager authManager) {
        this.adminRepository = adminRepository;
        this.jwtService = jwtService;
        this.authManager = authManager;
    }

    @Override
    public Admin updateUser(Admin admin) {
        return adminRepository.save(admin);
    }

    @Override
    @Transactional
    public void updateProfilePicture(Long adminId, byte[] pictureData) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        admin.setProfilePicture(pictureData);
        adminRepository.save(admin);
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
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    @Override
    public Admin createUser(Admin admin) {
        if(adminRepository.existsByEmail(admin.getEmail())) {
            throw  new RuntimeException("Email alredy in use!");
        }
        return adminRepository.save(admin);
    }

    @Override
    public Optional<Admin> getUserByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

    @Override
    public Optional<Admin> getUserById(Long id) {
        return adminRepository.findById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return adminRepository.existsByEmail(email);
    }


    @Override
    public void deleteUserById(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        adminRepository.deleteById(id);
    }
}
