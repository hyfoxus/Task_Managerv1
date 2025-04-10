package com.gnemirko.task_manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gnemirko.task_manager.entity.AuthRequest;
import com.gnemirko.task_manager.entity.User;
import com.gnemirko.task_manager.enums.Role;
import com.gnemirko.task_manager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll(); // очищаем базу перед каждым тестом
  }

  @Test
  void register_shouldReturnUser() throws Exception {
    AuthRequest request = new AuthRequest("test@example.com", "password123");

    mockMvc
            .perform(
                    post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
            .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("test@example.com"));
  }

  @Test
  void login_shouldReturnToken_whenCredentialsAreValid() throws Exception {
    AuthRequest request = new AuthRequest("test@example.com", "password123");

    // Register first
    mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());

    // Login
    mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.notNullValue()));
  }

  @Test
  void login_shouldReturnUnauthorized_whenInvalid() throws Exception {
    AuthRequest request = new AuthRequest("invalid@example.com", "wrongpassword");

    mockMvc
            .perform(
                    post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized());
  }
}
