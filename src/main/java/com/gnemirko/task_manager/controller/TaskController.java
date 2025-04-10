package com.gnemirko.task_manager.controller;

import com.gnemirko.task_manager.entity.Task;
import com.gnemirko.task_manager.entity.User;
import com.gnemirko.task_manager.enums.TaskPriority;
import com.gnemirko.task_manager.enums.TaskStatus;
import com.gnemirko.task_manager.repository.TaskRepository;
import com.gnemirko.task_manager.repository.UserRepository;
import java.util.List;

import com.gnemirko.task_manager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;


    private final TaskService taskService;

    @GetMapping("/author/{authorId}")
    public List<Task> getTasksByAuthor(@PathVariable Long authorId) {
        User author = userRepository.findById(authorId).orElseThrow();
        return taskRepository.findByAuthor(author);
    }

    @GetMapping("/assignee/{assigneeId}")
    public List<Task> getTasksByAssignee(@PathVariable Long assigneeId) {
        User assignee = userRepository.findById(assigneeId).orElseThrow();
        return taskRepository.findByAssignee(assignee);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task created = taskService.createTask(task);
        return ResponseEntity.ok(created);
    }

    // Полное редактирование задачи — только ADMIN и назначенный пользователь
    @PutMapping("/{taskId}")
    @PreAuthorize("hasRole('ADMIN') or @taskSecurity.isAssignee(authentication, #taskId)")
    public ResponseEntity<Task> editTask(@PathVariable Long taskId,
                                         @RequestParam String title,
                                         @RequestParam String description,
                                         @RequestParam TaskStatus status,
                                         @RequestParam TaskPriority priority) {
        Task task = taskService.editTask(taskId, title, description, status, priority);
        return ResponseEntity.ok(task);
    }

    @PatchMapping("/{taskId}/title")
    @PreAuthorize("hasRole('ADMIN') or @taskSecurity.isAssignee(authentication, #taskId)")
    public ResponseEntity<Task> updateTitle(@PathVariable Long taskId,
                                            @RequestParam String title) {
        return ResponseEntity.ok(taskService.updateTitle(taskId, title));
    }

    @PatchMapping("/{taskId}/description")
    @PreAuthorize("hasRole('ADMIN') or @taskSecurity.isAssignee(authentication, #taskId)")
    public ResponseEntity<Task> updateDescription(@PathVariable Long taskId,
                                                  @RequestParam String description) {
        return ResponseEntity.ok(taskService.updateDescription(taskId, description));
    }

    @PatchMapping("/{taskId}/status")
    @PreAuthorize("hasRole('ADMIN') or @taskSecurity.isAssignee(authentication, #taskId)")
    public ResponseEntity<Task> updateStatus(@PathVariable Long taskId,
                                             @RequestParam TaskStatus status) {
        return ResponseEntity.ok(taskService.updateStatus(taskId, status));
    }

    @PatchMapping("/{taskId}/priority")
    @PreAuthorize("hasRole('ADMIN') or @taskSecurity.isAssignee(authentication, #taskId)")
    public ResponseEntity<Task> updatePriority(@PathVariable Long taskId,
                                               @RequestParam TaskPriority priority) {
        return ResponseEntity.ok(taskService.updatePriority(taskId, priority));
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskRepository.deleteById(id);
    }
}
