package com.example.eLearningPlatform.models.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "admins")
@Getter
@Setter
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "admin_id", referencedColumnName = "user_id")
public class Admin extends User {
}
