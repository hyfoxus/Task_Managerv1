package com.gnemirko.task_manager;

import com.gnemirko.task_manager.security.JwtTokenProvider;
import com.gnemirko.task_manager.security.TaskSecurity;
import com.gnemirko.task_manager.service.AuthService;
import com.gnemirko.task_manager.service.TaskService;
import com.gnemirko.task_manager.service.UserService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@TestConfiguration

public class TestSecurityConfig {
  @Bean
  public AuthService authService() {
    return Mockito.mock(AuthService.class);
  }

  @Bean
  public JwtTokenProvider jwtTokenProvider() {
    return Mockito.mock(JwtTokenProvider.class);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // Аналогично для других контроллеров:
  @Bean
  public TaskService taskService() {
    return Mockito.mock(TaskService.class);
  }

  @Bean
  public TaskSecurity taskSecurity() {
    return Mockito.mock(TaskSecurity.class);
  }

  @Bean
  public UserService userService() {
    return Mockito.mock(UserService.class);
  }
}

