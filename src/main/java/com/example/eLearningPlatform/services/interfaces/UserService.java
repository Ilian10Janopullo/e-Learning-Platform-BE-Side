package com.example.eLearningPlatform.services.interfaces;

import com.example.eLearningPlatform.models.entities.Lecturer;
import com.example.eLearningPlatform.models.entities.User;

import java.util.Optional;

public interface UserService<T extends User> {
    T createUser(T user);
    Optional<T> getUserByEmail(String email);
    Optional<T> getUserById(Long id);
    T updateUser(T user);
    boolean existsByEmail(String email);
    void deleteUserById(Long id); // New delete method
    void updateProfilePicture(Long userId, byte[] pictureData);
    String verify(String username, String password);
}
