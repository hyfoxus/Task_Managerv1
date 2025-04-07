package com.gnemirko.task_manager.security;

import com.gnemirko.task_manager.entity.Task;
import com.gnemirko.task_manager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskSecurity {

  private final TaskRepository taskRepository;

  public boolean isAssignee(Authentication authentication, Long taskId) {
    String email = authentication.getName();
    Task task = taskRepository.findById(taskId).orElse(null);
    return task != null
        && task.getAssignee() != null
        && task.getAssignee().getEmail().equals(email);
  }
}
