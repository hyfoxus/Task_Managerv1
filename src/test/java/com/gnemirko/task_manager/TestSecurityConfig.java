package com.gnemirko.task_manager;

import com.gnemirko.task_manager.security.JwtTokenProvider;
import com.gnemirko.task_manager.service.AuthService;
import com.gnemirko.task_manager.service.TaskService;
import com.gnemirko.task_manager.service.UserService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestSecurityConfig {

  @Bean
  public TaskService taskService() {
    return Mockito.mock(TaskService.class);
  }

  @Bean
  public AuthService authService() {
    return Mockito.mock(AuthService.class);
  }

  @Bean
  public UserService userService() {
    return Mockito.mock(UserService.class);
  }

  @Bean
  public JwtTokenProvider jwtTokenProvider() {
    return Mockito.mock(JwtTokenProvider.class);
  }
}
