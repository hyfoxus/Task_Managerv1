package com.gnemirko.task_manager;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gnemirko.task_manager.controller.UserController;
import com.gnemirko.task_manager.entity.User;
import com.gnemirko.task_manager.enums.Role;
import com.gnemirko.task_manager.service.UserService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
class UserControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private UserService userService;

  private User adminUser;
  private User regularUser;

  @BeforeEach
  void setUp() {
    adminUser = new User(1L, "admin@example.com", "adminpass", Role.ADMIN);
    regularUser = new User(2L, "user@example.com", "userpass", Role.USER);
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void shouldReturnAllUsers_whenAdmin() throws Exception {
    when(userService.getAllUsers()).thenReturn(List.of(adminUser, regularUser));

    mockMvc
        .perform(get("/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].email").value("admin@example.com"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void shouldReturnUserById_whenAdmin() throws Exception {
    when(userService.getUserById(2L)).thenReturn(Optional.of(regularUser));

    mockMvc
        .perform(get("/users/2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("user@example.com"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void shouldReturnUsersByRole_whenAdmin() throws Exception {
    when(userService.getUsersByRole(Role.USER)).thenReturn(List.of(regularUser));

    mockMvc
        .perform(get("/users/role/USER"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].role").value("USER"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void shouldDeleteUser_whenAdminAndExists() throws Exception {
    when(userService.getUserById(2L)).thenReturn(Optional.of(regularUser));

    mockMvc
        .perform(delete("/users/2"))
        .andExpect(status().isOk())
        .andExpect(content().string("User deleted"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void shouldReturnNotFound_whenDeletingNonExistentUser() throws Exception {
    when(userService.getUserById(999L)).thenReturn(Optional.empty());

    mockMvc.perform(delete("/users/999")).andExpect(status().isNotFound());
  }
}
