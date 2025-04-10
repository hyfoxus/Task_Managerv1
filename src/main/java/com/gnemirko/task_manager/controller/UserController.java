package com.gnemirko.task_manager.controller;

import com.gnemirko.task_manager.entity.User;
import com.gnemirko.task_manager.enums.Role;
import com.gnemirko.task_manager.repository.UserRepository;
import java.util.List;
import java.util.Optional;

import com.gnemirko.task_manager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {


  private final UserService userService;


  // Доступен только администратору
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<User>> getAllUsers() {
    return ResponseEntity.ok(userService.getAllUsers());
  }

  // Доступен администратору и самому пользователю (сравнение ID)
  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
  public ResponseEntity<User> getUserById(@PathVariable Long id) {
    return userService.getUserById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
  }

  // Только для администратора
  @GetMapping("/role/{role}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<User>> getUsersByRole(@PathVariable Role role) {
    return ResponseEntity.ok(userService.getUsersByRole(role));
  }

  // Удаление пользователя — только админ
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<String> deleteUser(@PathVariable Long id) {
    Optional<User> userOpt = userService.getUserById(id);
    if (userOpt.isEmpty()) return ResponseEntity.notFound().build();

    userService.deleteUser(id);
    return ResponseEntity.ok("User deleted");
  }

}
