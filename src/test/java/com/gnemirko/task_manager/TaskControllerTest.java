package com.gnemirko.task_manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gnemirko.task_manager.entity.AuthRequest;
import com.gnemirko.task_manager.entity.Task;
import com.gnemirko.task_manager.entity.User;
import com.gnemirko.task_manager.enums.Role;
import com.gnemirko.task_manager.enums.TaskPriority;
import com.gnemirko.task_manager.enums.TaskStatus;
import com.gnemirko.task_manager.repository.TaskRepository;
import com.gnemirko.task_manager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private UserRepository userRepository;
  @Autowired private TaskRepository taskRepository;
  @Autowired private PasswordEncoder passwordEncoder;

  private String adminToken;

  @BeforeEach
  void setUp() throws Exception {
    // Чистим базу
    taskRepository.deleteAll();
    userRepository.deleteAll();

    // Создаем админа напрямую в БД (ускоряет)
    User admin = new User();
    admin.setEmail("admin@example.com");
    admin.setHashedPassword(passwordEncoder.encode("admin123"));
    admin.setRole(Role.ADMIN);
    userRepository.save(admin);

    // Получаем токен
    AuthRequest request = new AuthRequest("admin@example.com", "admin123");

    var loginResponse = mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andReturn();

    adminToken = loginResponse.getResponse().getContentAsString();
  }

  @Test
  void shouldCreateTask_whenAdmin() throws Exception {
    mockMvc.perform(post("/api/tasks")
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                                "title": "New Task",
                                "description": "Test description",
                                "status": "PENDING",
                                "priority": "HIGH"
                            }
                        """))

            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title", is("New Task")))
            .andExpect(jsonPath("$.status", is("PENDING")))
            .andExpect(jsonPath("$.priority", is("HIGH")));
  }
}