package com.example.eLearningPlatform.services.interfaces;

import com.example.eLearningPlatform.models.entities.Certificate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CertificateService {
    Certificate issueCertificate(Long studentId, Long courseId);
    List<Certificate> getCertificatesByStudentId(Long studentId);
    Optional<Certificate> getCertificateByStudentAndCourse(Long studentId, Long courseId);
    long countCertificatesByStudent(Long studentId);
    long countCertificatesByCourse(Long courseId);
    Certificate getCertificateById(UUID certificateId);
    byte[] getCertificateAsPng(UUID certificateId);
}


