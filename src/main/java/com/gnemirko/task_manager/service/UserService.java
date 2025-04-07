package com.gnemirko.task_manager.service;

import com.gnemirko.task_manager.entity.User;
import com.gnemirko.task_manager.enums.Role;
import com.gnemirko.task_manager.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Autowired private UserRepository userRepository;

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  public List<User> getUsersByRole(Role role) {
    return userRepository.findByRole(role);
  }

  public Optional<User> getUserById(Long id) {
    return userRepository.findById(id);
  }

  public Optional<User> getUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  public void deleteUser(Long id) {
    userRepository.deleteById(id);
  }

}
