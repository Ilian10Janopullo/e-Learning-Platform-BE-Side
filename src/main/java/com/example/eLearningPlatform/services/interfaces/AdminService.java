package com.example.eLearningPlatform.services.interfaces;

import com.example.eLearningPlatform.models.entities.Admin;
import java.util.List;
import java.util.Optional;

public interface AdminService extends UserService<Admin> {
    List<Admin> getAllAdmins();
}

