package com.example.eLearningPlatform.controllers.dto.respones;

import com.example.eLearningPlatform.models.enums.AccountStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private AccountStatus accountStatus;
    private LocalDate enrollmentDate;
    private byte[] profilePicture;
    private String stripeAccountId = null;
    private String jwtToken = null;
}
