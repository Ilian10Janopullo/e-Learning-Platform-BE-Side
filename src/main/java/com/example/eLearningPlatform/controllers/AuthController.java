package com.example.eLearningPlatform.controllers;

import com.example.eLearningPlatform.config.services.EmailService;
import com.example.eLearningPlatform.controllers.dto.*;
import com.example.eLearningPlatform.controllers.dto.LecturerRegistrationDTO;
import com.example.eLearningPlatform.controllers.dto.StudentRegistrationDTO;
import com.example.eLearningPlatform.controllers.dto.respones.UserResponseDTO;
import com.example.eLearningPlatform.models.entities.User;
import com.example.eLearningPlatform.models.enums.AccountStatus;
import com.example.eLearningPlatform.utils.SecurityUtil;
import jakarta.validation.Valid;
import com.example.eLearningPlatform.models.entities.Admin;
import com.example.eLearningPlatform.models.entities.Lecturer;
import com.example.eLearningPlatform.models.entities.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.eLearningPlatform.services.interfaces.AdminService;
import com.example.eLearningPlatform.services.interfaces.LecturerService;
import com.example.eLearningPlatform.services.interfaces.StudentService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.example.eLearningPlatform.utils.EmailMessages.RegisterBodyMessage;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final StudentService studentService;
    private final LecturerService lecturerService;
    private final AdminService adminService;
    private final EmailService emailService;
    private final SecurityUtil securityUtil;

    @Autowired
    public AuthController(StudentService studentService, LecturerService lecturerService, AdminService adminService, EmailService emailService, SecurityUtil securityUtil) {
        this.studentService = studentService;
        this.lecturerService = lecturerService;
        this.adminService = adminService;
        this.emailService = emailService;
        this.securityUtil = securityUtil;
    }

    // ----- Registration Endpoints -----
    @PostMapping("/register/student")
    public ResponseEntity<Map<String, Object>> registerStudent(@RequestBody @Valid StudentRegistrationDTO dto) {
        Student student = new Student();
        student.setUsername(dto.getUsername());
        student.setPassword(dto.getPassword());
        student.setEmail(dto.getEmail());
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());

        Student created = studentService.createUser(student);

        emailService.sendSimpleEmail(created.getEmail(), "e-learning Platform Student Registration Confirmation!", RegisterBodyMessage(student.getFirstName(), student.getLastName()));

        String token = studentService.verify(dto.getUsername(), dto.getPassword());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("role", "student");
        response.put("userId", created.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/register/lecturer")
    public ResponseEntity<Map<String, Object>> registerLecturer(@RequestBody @Valid LecturerRegistrationDTO dto) {
        Lecturer lecturer = new Lecturer();
        lecturer.setUsername(dto.getUsername());
        lecturer.setPassword(dto.getPassword());
        lecturer.setEmail(dto.getEmail());
        lecturer.setFirstName(dto.getFirstName());
        lecturer.setLastName(dto.getLastName());
        lecturer.setDescription(dto.getDescription());
        lecturer.setStripeAccountId(dto.getStripeAccountId());

        Lecturer created = lecturerService.createUser(lecturer);

        emailService.sendSimpleEmail(created.getEmail(), "e-learning Platform Lecturer Registration Confirmation!", RegisterBodyMessage(lecturer.getFirstName(), lecturer.getLastName()));

        String token = lecturerService.verify(dto.getUsername(), dto.getPassword());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("role", "lecturer");
        response.put("userId", created.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ----- Login Endpoint -----
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody @Valid LoginDTO dto) {
        Map<String, Object> response = new HashMap<>();

        Optional<Student> studentOpt = studentService.getUserByEmail(dto.getEmail());
        if (studentOpt.isPresent()) {

            if(studentOpt.get().getAccountStatus().equals(AccountStatus.DISABLED)){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = studentService.verify(studentOpt.get().getUsername(), dto.getPassword());

            response.put("token", token);
            response.put("role", "student");
            response.put("userId", studentOpt.get().getId());
            return ResponseEntity.ok(response);
        }

        Optional<Lecturer> lecturerOpt = lecturerService.getUserByEmail(dto.getEmail());
        if (lecturerOpt.isPresent()) {

            if(lecturerOpt.get().getAccountStatus().equals(AccountStatus.DISABLED)){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = lecturerService.verify(lecturerOpt.get().getUsername(), dto.getPassword());

            response.put("token", token);
            response.put("role", "lecturer");
            response.put("userId", lecturerOpt.get().getId());
            return ResponseEntity.ok(response);
        }

        Optional<Admin> adminOpt = adminService.getUserByEmail(dto.getEmail());
        if (adminOpt.isPresent()) {

            if(adminOpt.get().getAccountStatus().equals(AccountStatus.DISABLED)){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = adminService.verify(adminOpt.get().getUsername(), dto.getPassword());

            response.put("token", token);
            response.put("role", "admin");
            response.put("userId", adminOpt.get().getId());
            return ResponseEntity.ok(response);
        }

        response.put("message", "Invalid email or password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Logout successful! Please delete the token on the client side!");
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/get-user")
    public ResponseEntity<UserResponseDTO> getUser() {

        Long userId = securityUtil.getAuthenticatedUserId();

        if(lecturerService.getUserById(userId).isPresent()){
            UserResponseDTO userResponseDTO = getUserResponseDTO(lecturerService.getUserById(userId).get());
            userResponseDTO.setStripeAccountId(lecturerService.getUserById(userId).get().getStripeAccountId());
            return ResponseEntity.ok(userResponseDTO);
        } else if (studentService.getUserById(userId).isPresent()) {
            return ResponseEntity.ok(getUserResponseDTO(studentService.getUserById(userId).get()));
        } else if (adminService.getUserById(userId).isPresent()) {
            return ResponseEntity.ok(getUserResponseDTO(adminService.getUserById(userId).get()));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private static UserResponseDTO getUserResponseDTO(User user) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(user.getId());
        userResponseDTO.setUsername(user.getUsername());
        userResponseDTO.setEmail(user.getEmail());
        userResponseDTO.setFirstName(user.getFirstName());
        userResponseDTO.setLastName(user.getLastName());
        userResponseDTO.setAccountStatus(user.getAccountStatus());
        userResponseDTO.setEnrollmentDate(user.getEnrollmentDate());
        if(user.getProfilePicture() != null){
            userResponseDTO.setProfilePicture(user.getProfilePicture());
        }
        return userResponseDTO;
    }


}
