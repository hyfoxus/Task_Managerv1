package com.gnemirko.task_manager;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.gnemirko.task_manager.controller.TaskController;
import com.gnemirko.task_manager.entity.Task;
import com.gnemirko.task_manager.enums.TaskPriority;
import com.gnemirko.task_manager.enums.TaskStatus;
import com.gnemirko.task_manager.security.TaskSecurity;
import com.gnemirko.task_manager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TaskController.class)
@Import(TestSecurityConfig.class)
class TaskControllerTest {

  @Autowired private MockMvc mockMvc;

  private TaskService taskService;

  private TaskSecurity taskSecurity;

  private Task mockTask;

  @BeforeEach
  void setup() {
    mockTask = new Task();
    mockTask.setId(1L);
    mockTask.setTitle("Initial Task");
    mockTask.setDescription("Initial Description");
    mockTask.setStatus(TaskStatus.PENDING);
    mockTask.setPriority(TaskPriority.MEDIUM);
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void shouldCreateTask_whenAdmin() throws Exception {
    when(taskService.createTask(any(Task.class))).thenReturn(mockTask);

    mockMvc
        .perform(
            post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "title": "Initial Task",
                        "description": "Initial Description",
                        "status": "PENDING",
                        "priority": "MEDIUM"
                    }
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Initial Task"));
  }

  @Test
  @WithMockUser(username = "admin@example.com", roles = "ADMIN")
  void shouldEditTask_whenAdmin() throws Exception {
    when(taskService.editTask(anyLong(), any(), any(), any(), any())).thenReturn(mockTask);

    mockMvc
        .perform(
            put("/api/tasks/1")
                .param("title", "New Title")
                .param("description", "New Desc")
                .param("status", "IN_PROGRESS")
                .param("priority", "HIGH"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Initial Task")); // return mockTask
  }

  @Test
  @WithMockUser(username = "user@example.com")
  void shouldEditTask_whenAssignee() throws Exception {
    when(taskSecurity.isAssignee(any(), eq(1L))).thenReturn(true);
    when(taskService.editTask(eq(1L), any(), any(), any(), any())).thenReturn(mockTask);

    mockMvc
        .perform(
            put("/api/tasks/1")
                .param("title", "New Title")
                .param("description", "New Desc")
                .param("status", "IN_PROGRESS")
                .param("priority", "HIGH"))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "stranger@example.com")
  void shouldDenyEditTask_whenNotAdminAndNotAssignee() throws Exception {
    when(taskSecurity.isAssignee(any(), eq(1L))).thenReturn(false);

    mockMvc
        .perform(
            put("/api/tasks/1")
                .param("title", "New Title")
                .param("description", "New Desc")
                .param("status", "IN_PROGRESS")
                .param("priority", "HIGH"))
        .andExpect(status().isForbidden());
  }
}
