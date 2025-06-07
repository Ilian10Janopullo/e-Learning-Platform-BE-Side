package com.example.eLearningPlatform.repositories;

import com.example.eLearningPlatform.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface UserRepository<T extends User> extends JpaRepository<T, Long> {

    Optional<T> findByEmail(String email);

    Optional<T> findByUsername(String username);

    boolean existsByEmail(String email);
}
