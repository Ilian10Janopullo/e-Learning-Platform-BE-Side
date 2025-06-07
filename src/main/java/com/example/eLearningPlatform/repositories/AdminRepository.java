package com.example.eLearningPlatform.repositories;

import com.example.eLearningPlatform.models.entities.Admin;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends UserRepository<Admin> {
}
