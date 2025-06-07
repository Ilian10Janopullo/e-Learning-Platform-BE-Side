package com.example.eLearningPlatform.controllers;

import com.example.eLearningPlatform.config.services.EmailService;
import com.example.eLearningPlatform.controllers.dto.AdminRegistrationDTO;
import com.example.eLearningPlatform.controllers.dto.UpdateAdminDTO;
import com.example.eLearningPlatform.utils.ResponseMessage;
import jakarta.validation.Valid;
import com.example.eLearningPlatform.models.entities.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.eLearningPlatform.services.interfaces.AdminService;
import com.example.eLearningPlatform.utils.SecurityUtil;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/admins")
public class AdminsController {

    private final AdminService adminService;
    private final SecurityUtil securityUtil;
    private final EmailService emailService;;

    @Autowired
    public AdminsController(AdminService adminService, SecurityUtil securityUtil, EmailService emailService) {
        this.adminService = adminService;
        this.securityUtil = securityUtil;
        this.emailService = emailService;
    }

    @PostMapping(value = "/create-admin",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Admin> registerAdmin(@RequestPart("dto") @Valid AdminRegistrationDTO dto,
                                               @RequestPart(name = "profilePicture", required = false) MultipartFile profilePicture) {
        Admin admin = new Admin();
        admin.setUsername(dto.getUsername());
        admin.setPassword(dto.getPassword());
        admin.setEmail(dto.getEmail());
        admin.setFirstName(dto.getFirstName());
        admin.setLastName(dto.getLastName());

        if(profilePicture != null && !profilePicture.isEmpty()){

            try {
                byte[] pictureData = profilePicture.getBytes();
                admin.setProfilePicture(pictureData);
            } catch (IOException e) {
                throw new RuntimeException("Error converting file to byte array", e);
            }

        }

        Admin createdAdmin = adminService.createUser(admin);

        emailService.sendSimpleEmail(createdAdmin.getEmail(), "e-learning Platform Admin Registration Confirmation!", bodyMessage(createdAdmin.getFirstName(), createdAdmin.getLastName()));

        return ResponseEntity.status(HttpStatus.CREATED).body(createdAdmin);
    }

    @PutMapping(value = "/update",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Admin> updateAdmin(@RequestPart("dto") @Valid UpdateAdminDTO dto,
                                             @RequestPart(name = "profilePicture", required = false) MultipartFile profilePicture) {
        Long adminId = securityUtil.getAuthenticatedUserId();
        Optional<Admin> adminOpt = adminService.getUserById(adminId);
        if (adminOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Admin admin = adminOpt.get();
        admin.setUsername(dto.getUsername());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            admin.setPassword(dto.getPassword());
        }
        admin.setEmail(dto.getEmail());
        admin.setFirstName(dto.getFirstName());
        admin.setLastName(dto.getLastName());

        if(profilePicture != null && !profilePicture.isEmpty()){

            try {
                byte[] pictureData = profilePicture.getBytes();
                admin.setProfilePicture(pictureData);
            } catch (IOException e) {
                throw new RuntimeException("Error converting file to byte array", e);
            }

        }

        Admin updatedAdmin = adminService.updateUser(admin);
        return ResponseEntity.ok(updatedAdmin);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseMessage> deleteAdmin() {
        Long authenticatedAdminId = securityUtil.getAuthenticatedUserId();
        adminService.deleteUserById(authenticatedAdminId);
        return ResponseEntity.ok(new ResponseMessage("Admin account deleted successfully."));
    }

    public static String bodyMessage(String firstName, String lastName) {
        return "Dear " + firstName + " " + lastName +",\n" +
                "\n" +
                "We are pleased to inform you that your account at eLearning Platform has been successfully created. You may now log in to our platform and begin exploring our services.\n" +
                "\n" +
                "If you require any assistance or have any questions, please do not hesitate to contact our support team at epoka.learning@gmail.com.\n" +
                "\n" +
                "Thank you for choosing eLearning Platform. We look forward to serving you.\n" +
                "\n" +
                "Sincerely," +
                "eLearning Platform Admin\n";
    }
}
