package com.gnemirko.task_manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gnemirko.task_manager.entity.AuthRequest;
import com.gnemirko.task_manager.entity.User;
import com.gnemirko.task_manager.enums.Role;
import com.gnemirko.task_manager.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private UserRepository userRepository;
  @Autowired private PasswordEncoder passwordEncoder;

  private String adminToken;
  private Long userId;

  @BeforeEach
  void setup() throws Exception {
    userRepository.deleteAll();

    // Создаем админа
    User admin = new User();
    admin.setEmail("admin@example.com");
    admin.setHashedPassword(passwordEncoder.encode("admin123"));
    admin.setRole(Role.ADMIN);
    userRepository.save(admin);

    // Создаем обычного пользователя
    User user = new User();
    user.setEmail("user@example.com");
    user.setHashedPassword(passwordEncoder.encode("user123"));
    user.setRole(Role.USER);
    userId = userRepository.save(user).getId();

    // Логинимся админом
    AuthRequest request = new AuthRequest("admin@example.com", "admin123");
    String token = mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

    adminToken = token;
  }

  @Test
  @Transactional
  void shouldReturnAllUsers_whenAdmin() throws Exception {
    mockMvc.perform(get("/api/users")
                    .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  @Transactional
  void shouldReturnUserById_whenAdmin() throws Exception {
    mockMvc.perform(get("/api/users/" + userId)
                    .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email", is("user@example.com")));
  }

  @Test
  @Transactional
  void shouldReturnUsersByRole_whenAdmin() throws Exception {
    mockMvc.perform(get("/api/users/role/USER")
                    .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].role", is("USER")));
  }

  @Test
  @Transactional
  void shouldDeleteUser_whenAdminAndExists() throws Exception {
    mockMvc.perform(delete("/api/users/" + userId)
                    .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());

    mockMvc.perform(get("/api/users/" + userId)
                    .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isNotFound());
  }

  @Test
  @Transactional
  void shouldReturnNotFound_whenDeletingNonExistentUser() throws Exception {
    mockMvc.perform(delete("/api/users/9999")
                    .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isNotFound());
  }
}
