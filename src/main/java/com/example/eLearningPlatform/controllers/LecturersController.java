package com.example.eLearningPlatform.controllers;

import com.example.eLearningPlatform.config.services.JwtService;
import com.example.eLearningPlatform.controllers.dto.UpdateLecturerDTO;
import com.example.eLearningPlatform.controllers.dto.respones.UserResponseDTO;
import com.example.eLearningPlatform.models.entities.Course;
import com.example.eLearningPlatform.services.interfaces.CourseService;
import com.example.eLearningPlatform.utils.ResponseMessage;
import jakarta.validation.Valid;
import com.example.eLearningPlatform.models.entities.Lecturer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.eLearningPlatform.services.interfaces.LecturerService;
import com.example.eLearningPlatform.utils.SecurityUtil;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lecturers")
public class LecturersController {

    private final LecturerService lecturerService;
    private final SecurityUtil securityUtil;
    private final CourseService courseService;
    private final JwtService jwtService;

    @Autowired
    public LecturersController(LecturerService lecturerService, SecurityUtil securityUtil, CourseService courseService, JwtService jwtService) {
        this.lecturerService = lecturerService;
        this.securityUtil = securityUtil;
        this.courseService = courseService;
        this.jwtService = jwtService;
    }

    @PutMapping(value = "/update",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponseDTO> updateLecturer(@RequestPart("dto") @Valid UpdateLecturerDTO dto,
                                                          @RequestPart(name = "profilePicture", required = false) MultipartFile profilePicture) {

        Long lecturerId = securityUtil.getAuthenticatedUserId();

        Optional<Lecturer> lecturerOpt = lecturerService.getUserById(lecturerId);
        if (lecturerOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Lecturer lecturer = lecturerOpt.get();
        lecturer.setUsername(dto.getUsername());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            lecturer.setPassword(dto.getPassword());
        }
        lecturer.setEmail(dto.getEmail());
        lecturer.setFirstName(dto.getFirstName());
        lecturer.setLastName(dto.getLastName());

        if(profilePicture != null && !profilePicture.isEmpty()){

            try {
                byte[] pictureData = profilePicture.getBytes();
                lecturer.setProfilePicture(pictureData);
            } catch (IOException e) {
                throw new RuntimeException("Error converting file to byte array", e);
            }

        }

        Lecturer updatedLecturer = lecturerService.updateUser(lecturer);

        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(updatedLecturer.getId());
        userResponseDTO.setUsername(updatedLecturer.getUsername());
        userResponseDTO.setEmail(updatedLecturer.getEmail());
        userResponseDTO.setFirstName(updatedLecturer.getFirstName());
        userResponseDTO.setLastName(updatedLecturer.getLastName());
        userResponseDTO.setAccountStatus(updatedLecturer.getAccountStatus());
        userResponseDTO.setEnrollmentDate(updatedLecturer.getEnrollmentDate());
        userResponseDTO.setStripeAccountId(updatedLecturer.getStripeAccountId());
        if(updatedLecturer.getProfilePicture() != null){
            userResponseDTO.setProfilePicture(updatedLecturer.getProfilePicture());
        }
        userResponseDTO.setJwtToken(jwtService.generateToken(updatedLecturer.getUsername()));

        return ResponseEntity.ok(userResponseDTO);
    }



    @DeleteMapping("/delete")
    public ResponseEntity<ResponseMessage> deleteLecturer() {
        Long authenticatedLecturerId = securityUtil.getAuthenticatedUserId();

        List<Course> courseList = courseService.getCoursesByLecturerId(authenticatedLecturerId);

        for(Course course : courseList){
            course.setLecturer(null);
        }

        lecturerService.deleteUserById(authenticatedLecturerId);
        return ResponseEntity.ok(new ResponseMessage("Lecturer account deleted successfully."));
    }
}
