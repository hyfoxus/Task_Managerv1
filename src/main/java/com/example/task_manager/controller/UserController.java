package com.example.task_manager.controller;

import com.example.task_manager.entity.User;
import com.example.task_manager.enums.Role;
import com.example.task_manager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

  @Autowired private UserRepository userRepository;

  @GetMapping
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  @GetMapping("/role/{role}")
  public List<User> getUsersByRole(@PathVariable Role role) {
    return userRepository.findByRole(role);
  }

  @GetMapping("/{id}")
  public User getUserById(@PathVariable Long id) {
    return userRepository.findById(id).orElseThrow();
  }
}
