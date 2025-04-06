package com.gnemirko.task_manager.entity;

import com.gnemirko.task_manager.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_user")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String email;
  private String hashedPassword; // change to Hash for more security

  @Enumerated(EnumType.STRING)
  private Role role;
}
