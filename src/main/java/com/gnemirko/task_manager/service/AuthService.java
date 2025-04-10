package com.gnemirko.task_manager.service;

import com.gnemirko.task_manager.entity.AuthRequest;
import com.gnemirko.task_manager.entity.User;
import com.gnemirko.task_manager.enums.Role;
import com.gnemirko.task_manager.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  @Autowired private UserRepository userRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  public User register(AuthRequest request) {
    User user = new User();
    user.setEmail(request.getEmail());
    user.setHashedPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(Role.USER);
    return userRepository.save(user);
  }

  public boolean login(AuthRequest request) {
    User user =
        userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new EntityNotFoundException("No User Found!"));
    return passwordEncoder.matches(request.getPassword(), user.getHashedPassword());
  }
}
