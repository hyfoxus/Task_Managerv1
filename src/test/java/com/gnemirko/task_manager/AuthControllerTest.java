package com.gnemirko.task_manager;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gnemirko.task_manager.controller.AuthController;
import com.gnemirko.task_manager.entity.AuthRequest;
import com.gnemirko.task_manager.entity.User;
import com.gnemirko.task_manager.enums.Role;
import com.gnemirko.task_manager.security.JwtTokenProvider;
import com.gnemirko.task_manager.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private AuthService authService;
  @Autowired private JwtTokenProvider jwtTokenProvider;

  private AuthRequest request;

  @BeforeEach
  void setUp() {
    request = new AuthRequest("test@example.com", "password123");

    when(authService.register(any()))
        .thenReturn(new User(1L, "test@example.com", "hashed", Role.USER));

    when(authService.login(any())).thenReturn(true);
    when(jwtTokenProvider.createToken("test@example.com")).thenReturn("mocked-token");
  }

  @Test
  void register_shouldReturnUser() throws Exception {
    mockMvc
        .perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("test@example.com"));
  }

  @Test
  void login_shouldReturnToken_whenCredentialsAreValid() throws Exception {
    mockMvc
        .perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().string("mocked-token"));
  }

  @Test
  void login_shouldReturnUnauthorized_whenInvalid() throws Exception {
    when(authService.login(any())).thenReturn(false);

    mockMvc
        .perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }
}
