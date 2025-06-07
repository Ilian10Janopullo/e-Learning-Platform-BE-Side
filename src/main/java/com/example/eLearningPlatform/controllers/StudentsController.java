package com.example.eLearningPlatform.controllers;

import com.example.eLearningPlatform.config.services.JwtService;
import com.example.eLearningPlatform.controllers.dto.UpdateStudentDTO;
import com.example.eLearningPlatform.controllers.dto.respones.UserResponseDTO;
import com.example.eLearningPlatform.models.entities.Lecturer;
import com.example.eLearningPlatform.utils.ResponseMessage;
import jakarta.validation.Valid;
import com.example.eLearningPlatform.models.entities.Course;
import com.example.eLearningPlatform.models.entities.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.eLearningPlatform.services.interfaces.StudentService;
import com.example.eLearningPlatform.utils.SecurityUtil;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/students")
public class StudentsController {

    private final StudentService studentService;
    private final SecurityUtil securityUtil;
    private final JwtService jwtService;

    @Autowired
    public StudentsController(StudentService studentService, SecurityUtil securityUtil, JwtService jwtService) {
        this.studentService = studentService;
        this.securityUtil = securityUtil;
        this.jwtService = jwtService;
    }

    @PutMapping(value = "/update",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponseDTO> updateStudent(@RequestPart("dto") @Valid UpdateStudentDTO dto,
                                                         @RequestPart(name = "profilePicture", required = false) MultipartFile profilePicture) {
        Long studentId = securityUtil.getAuthenticatedUserId();
        Optional<Student> studentOpt = studentService.getUserById(studentId);
        if (studentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Student student = studentOpt.get();

        student.setUsername(dto.getUsername());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            student.setPassword(dto.getPassword());
        }
        student.setEmail(dto.getEmail());
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());

        if(profilePicture != null && !profilePicture.isEmpty()){

            try {
                byte[] pictureData = profilePicture.getBytes();
                student.setProfilePicture(pictureData);
            } catch (IOException e) {
                throw new RuntimeException("Error converting file to byte array", e);
            }

        }

        Student updatedStudent = studentService.updateUser(student);

        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(updatedStudent.getId());
        userResponseDTO.setUsername(updatedStudent.getUsername());
        userResponseDTO.setEmail(updatedStudent.getEmail());
        userResponseDTO.setFirstName(updatedStudent.getFirstName());
        userResponseDTO.setLastName(updatedStudent.getLastName());
        userResponseDTO.setAccountStatus(updatedStudent.getAccountStatus());
        userResponseDTO.setEnrollmentDate(updatedStudent.getEnrollmentDate());
        if(updatedStudent.getProfilePicture() != null){
            userResponseDTO.setProfilePicture(updatedStudent.getProfilePicture());
        }
        userResponseDTO.setJwtToken(jwtService.generateToken(updatedStudent.getUsername()));

        return ResponseEntity.ok(userResponseDTO);
    }


    @DeleteMapping("/delete")
    public ResponseEntity<ResponseMessage> deleteStudent() {

        Long authenticatedStudentId = securityUtil.getAuthenticatedUserId();

        studentService.deleteUserById(authenticatedStudentId);
        return ResponseEntity.ok(new ResponseMessage("Student account deleted successfully."));
    }
}
