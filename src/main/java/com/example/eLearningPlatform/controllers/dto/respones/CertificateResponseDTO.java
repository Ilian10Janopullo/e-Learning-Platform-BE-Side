package com.example.eLearningPlatform.controllers.dto.respones;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CertificateResponseDTO {

    private UUID id;
    private String courseName;
    private String studentName;
    private LocalDateTime issuedAt;
    private byte[] certificatePdfUrl;

}
