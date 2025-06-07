package com.example.eLearningPlatform.controllers;

import com.example.eLearningPlatform.controllers.dto.respones.CertificateResponseDTO;
import com.example.eLearningPlatform.models.entities.Certificate;
import com.example.eLearningPlatform.repositories.CertificateRepository;
import com.example.eLearningPlatform.utils.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.eLearningPlatform.services.interfaces.CertificateService;
import com.example.eLearningPlatform.utils.SecurityUtil;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {

    private final CertificateService certificateService;
    private final SecurityUtil securityUtil;
    private final CertificateRepository certificateRepository;

    @Autowired
    public CertificateController(CertificateService certificateService, SecurityUtil securityUtil, CertificateRepository certificateRepository) {
        this.certificateService = certificateService;
        this.securityUtil = securityUtil;
        this.certificateRepository = certificateRepository;
    }

    @GetMapping
    public ResponseEntity<List<CertificateResponseDTO>> listCertificates() {
        Long studentId = securityUtil.getAuthenticatedUserId();
        List<Certificate> certificates = certificateService.getCertificatesByStudentId(studentId);

        List<CertificateResponseDTO> certificateResponseDTOS = new ArrayList<>();

        for (Certificate certificate : certificates) {
            CertificateResponseDTO dto = new CertificateResponseDTO();
            dto.setId(certificate.getId());
            dto.setStudentName(certificate.getStudent().getFirstName() + " " + certificate.getStudent().getLastName());
            dto.setCourseName(certificate.getCourse().getName());
            dto.setIssuedAt(certificate.getIssuedAt());
            dto.setCertificatePdfUrl(certificate.getCertificatePdf());
            certificateResponseDTOS.add(dto);
        }

        return ResponseEntity.ok(certificateResponseDTOS);
    }

    @GetMapping(value = "/{certificateId}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadCertificatePdf(@PathVariable UUID certificateId) {
        Certificate certificate = certificateService.getCertificateById(certificateId);
        return ResponseEntity.ok(certificate.getCertificatePdf());
    }

    @GetMapping(value = "/{certificateId}/png", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> downloadCertificatePng(@PathVariable UUID certificateId) {
        byte[] pngData = certificateService.getCertificateAsPng(certificateId);
        return ResponseEntity.ok(pngData);
    }

    @GetMapping("/validate/{certificateId}")
    public ResponseEntity<?> validateCertificate(@PathVariable UUID certificateId) {

        Optional<Certificate> certificateOpt = certificateRepository.findById(certificateId);

        if(certificateOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificate not found"));
        }

        Certificate certificate = certificateOpt.get();

        CertificateResponseDTO dto = new CertificateResponseDTO();
        dto.setId(certificate.getId());
        dto.setStudentName(certificate.getStudent().getFirstName() + " " + certificate.getStudent().getLastName());
        dto.setCourseName(certificate.getCourse().getName());
        dto.setIssuedAt(certificate.getIssuedAt());
        dto.setCertificatePdfUrl(certificate.getCertificatePdf());

        return ResponseEntity.ok(dto);
    }
}

