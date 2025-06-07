package com.example.eLearningPlatform.controllers;

import com.example.eLearningPlatform.config.services.EmailService;
import com.example.eLearningPlatform.controllers.dto.AccountStatusDTO;
import com.example.eLearningPlatform.controllers.dto.respones.UserResponseDTO;
import com.example.eLearningPlatform.models.entities.Admin;
import com.example.eLearningPlatform.models.entities.Lecturer;
import com.example.eLearningPlatform.models.entities.Student;
import com.example.eLearningPlatform.models.enums.AccountStatus;
import com.example.eLearningPlatform.services.interfaces.AdminService;
import com.example.eLearningPlatform.services.interfaces.LecturerService;
import com.example.eLearningPlatform.services.interfaces.StudentService;
import com.example.eLearningPlatform.utils.ResponseMessage;
import com.example.eLearningPlatform.utils.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.example.eLearningPlatform.utils.EmailMessages.AdminBodyMessage;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {
    private final StudentService studentService;
    private final LecturerService lecturerService;
    private final AdminService adminService;
    private final SecurityUtil securityUtil;
    private final EmailService emailService;

    @Autowired
    public AdminUserController(StudentService studentService, LecturerService lecturerService, AdminService adminService, SecurityUtil securityUtil, EmailService emailService) {

        this.studentService = studentService;
        this.lecturerService = lecturerService;
        this.adminService = adminService;
        this.securityUtil = securityUtil;
        this.emailService = emailService;
    }

    @GetMapping("/students")
    public ResponseEntity<List<UserResponseDTO>> getAllStudents() {

        Long userId = securityUtil.getAuthenticatedUserId();
        Optional<Admin> admin = adminService.getUserById(userId);

        if (admin.isEmpty()) {
            throw new RuntimeException("You are not an admin!");
        }

        List<Student> students = studentService.getAllStudents();
        List<UserResponseDTO> studentResponseDTOs = new ArrayList<>();

        for(Student student : students){
            UserResponseDTO studentResponseDTO = new UserResponseDTO();
            studentResponseDTO.setId(student.getId());
            studentResponseDTO.setUsername(student.getUsername());
            studentResponseDTO.setFirstName(student.getFirstName());
            studentResponseDTO.setLastName(student.getLastName());
            studentResponseDTO.setEmail(student.getEmail());
            studentResponseDTO.setAccountStatus(student.getAccountStatus());
            studentResponseDTO.setProfilePicture(student.getProfilePicture());
            studentResponseDTO.setEnrollmentDate(student.getEnrollmentDate());
            studentResponseDTOs.add(studentResponseDTO);
        }

        return new ResponseEntity<>(studentResponseDTOs, HttpStatus.OK);
    }

    @GetMapping("/lecturers")
    public ResponseEntity<List<UserResponseDTO>> getAllLecturers() {

        Long userId = securityUtil.getAuthenticatedUserId();
        Optional<Admin> admin = adminService.getUserById(userId);

        if (admin.isEmpty()) {
            throw new RuntimeException("You are not an admin!");
        }

        List<Lecturer> lecturers = lecturerService.getAllLecturers();

        List<UserResponseDTO> lecturersResponseDTOs = new ArrayList<>();

        for(Lecturer lecturer : lecturers){
            UserResponseDTO lecturerResponseDTO = new UserResponseDTO();
            lecturerResponseDTO.setId(lecturer.getId());
            lecturerResponseDTO.setUsername(lecturer.getUsername());
            lecturerResponseDTO.setFirstName(lecturer.getFirstName());
            lecturerResponseDTO.setLastName(lecturer.getLastName());
            lecturerResponseDTO.setEmail(lecturer.getEmail());
            lecturerResponseDTO.setAccountStatus(lecturer.getAccountStatus());
            lecturerResponseDTO.setProfilePicture(lecturer.getProfilePicture());
            lecturerResponseDTO.setEnrollmentDate(lecturer.getEnrollmentDate());
            lecturersResponseDTOs.add(lecturerResponseDTO);
        }

        return new ResponseEntity<>(lecturersResponseDTOs, HttpStatus.OK);
    }

    @GetMapping("/admins")
    public ResponseEntity<List<UserResponseDTO>> getAllAdmins() {

        Long userId = securityUtil.getAuthenticatedUserId();
        Optional<Admin> admin = adminService.getUserById(userId);

        if (admin.isEmpty()) {
            throw new RuntimeException("You are not an admin!");
        }

        List<Admin> admins = adminService.getAllAdmins();

        List<UserResponseDTO> adminResponseDTOs = new ArrayList<>();

        for(Admin admin1 : admins){
            UserResponseDTO adminResponseDTO = new UserResponseDTO();
            adminResponseDTO.setId(admin1.getId());
            adminResponseDTO.setUsername(admin1.getUsername());
            adminResponseDTO.setFirstName(admin1.getFirstName());
            adminResponseDTO.setLastName(admin1.getLastName());
            adminResponseDTO.setEmail(admin1.getEmail());
            adminResponseDTO.setAccountStatus(admin1.getAccountStatus());
            adminResponseDTO.setProfilePicture(admin1.getProfilePicture());
            adminResponseDTO.setEnrollmentDate(admin1.getEnrollmentDate());
            adminResponseDTOs.add(adminResponseDTO);
        }

        return new ResponseEntity<>(adminResponseDTOs, HttpStatus.OK);
    }

    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<ResponseMessage> deleteUser(@PathVariable Long id) {

        Long userId = securityUtil.getAuthenticatedUserId();
        Optional<Admin> admin = adminService.getUserById(userId);

        if (admin.isEmpty()) {
            throw new RuntimeException("You are not an admin!");
        }

        Optional<Student> student = studentService.getUserById(id);
        if (student.isPresent()) {
            studentService.deleteUserById(id);
            emailService.sendSimpleEmail(student.get().getEmail(), "Account Deletion Notification", AdminBodyMessage(student.get().getFirstName(), student.get().getLastName()) );
            return ResponseEntity.ok(new ResponseMessage("Account deleted successfully."));
        }

        Optional<Lecturer> lecturer = lecturerService.getUserById(id);
        if (lecturer.isPresent()) {
            lecturerService.deleteUserById(id);
            emailService.sendSimpleEmail(lecturer.get().getEmail(), "Account Deletion Notification", AdminBodyMessage(lecturer.get().getFirstName(), lecturer.get().getLastName()) );
            return ResponseEntity.ok(new ResponseMessage("Account deleted successfully."));
        }

        Optional<Admin> admin1 = adminService.getUserById(id);
        if (admin1.isPresent()) {
            adminService.deleteUserById(id);
            emailService.sendSimpleEmail(admin1.get().getEmail(), "Account Deletion Notification", AdminBodyMessage(admin1.get().getFirstName(), admin1.get().getLastName()) );
            return ResponseEntity.ok(new ResponseMessage("Account deleted successfully."));
        }

        return ResponseEntity.ok(new ResponseMessage("No account got deleted!."));

    }

    @PutMapping("/change-account-status/{id}")
    public ResponseEntity<ResponseMessage> changeStatus(@PathVariable Long id, @RequestBody @Valid AccountStatusDTO dto) {

        Long userId = securityUtil.getAuthenticatedUserId();
        Optional<Admin> admin = adminService.getUserById(userId);

        if (admin.isEmpty()) {
            throw new RuntimeException("You are not an admin!");
        }

        Optional<Student> student = studentService.getUserById(id);
        if (student.isPresent()) {

            Student student1 = student.get();

            if(student1.getAccountStatus().equals(dto.getStatus())) {
                return ResponseEntity.ok(new ResponseMessage("Account status is still the same!"));
            }

            student1.setAccountStatus(dto.getStatus());
            studentService.updateUser(student1);
            emailService.sendSimpleEmail(student.get().getEmail(), "Change of Account Status", AdminBodyMessage(student.get().getFirstName(), student.get().getLastName(), dto.getStatus()));
            return ResponseEntity.ok(new ResponseMessage("Account status changed successfully."));
        }

        Optional<Lecturer> lecturer = lecturerService.getUserById(id);
        if (lecturer.isPresent()) {

            Lecturer lecturer1 = lecturer.get();

            if(lecturer1.getAccountStatus().equals(dto.getStatus())) {
                return ResponseEntity.ok(new ResponseMessage("Account status is still the same!"));
            }

            lecturer1.setAccountStatus(dto.getStatus());
            lecturerService.updateUser(lecturer1);
            emailService.sendSimpleEmail(lecturer.get().getEmail(), "Change of Account Status", AdminBodyMessage(lecturer.get().getFirstName(), lecturer.get().getLastName(), dto.getStatus()));
            return ResponseEntity.ok(new ResponseMessage("Account status changed successfully."));
        }

        Optional<Admin> admin1 = adminService.getUserById(id);
        if (admin1.isPresent()) {

            Admin admin2 = admin1.get();

            if(admin2.getAccountStatus().equals(dto.getStatus())) {
                return ResponseEntity.ok(new ResponseMessage("Account status is still the same!"));
            }

            admin2.setAccountStatus(dto.getStatus());
            adminService.updateUser(admin2);
            emailService.sendSimpleEmail(admin1.get().getEmail(), "Change of Account Status", AdminBodyMessage(admin1.get().getFirstName(), admin1.get().getLastName(), dto.getStatus()));
            return ResponseEntity.ok(new ResponseMessage("Account status changed successfully."));
        }

        return ResponseEntity.ok(new ResponseMessage("No account got deleted!."));

    }


}
