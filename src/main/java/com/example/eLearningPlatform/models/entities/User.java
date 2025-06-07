package com.example.eLearningPlatform.models.entities;

import com.example.eLearningPlatform.models.enums.AccountStatus;
import com.example.eLearningPlatform.utils.SaltUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "password")
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
    @Column(unique = true, nullable = false, name = "username")
    private String username;

    @JsonIgnore
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
    )
    @Column(nullable = false, name = "user_password")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(unique = true, nullable = false, name = "email")
    private String email;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50)
    @Column(name = "first_name")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50)
    @Column(name = "last_name")
    private String lastName;

    @Column(nullable = false, name = "enrollment_date")
    private LocalDate enrollmentDate;

    @Column(name = "profile_picture", columnDefinition = "LONGBLOB")
    @Lob
    private byte[] profilePicture;

    @Column(name = "account_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus = AccountStatus.ENABLED;

    public void setPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(SaltUtil.getSaltedPassword(password));
    }

    @PrePersist
    protected void onCreate() {
        this.enrollmentDate = LocalDate.now();
    }

}
