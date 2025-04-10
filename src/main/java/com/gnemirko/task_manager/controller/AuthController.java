package com.gnemirko.task_manager.controller;

import com.gnemirko.task_manager.entity.AuthRequest;
import com.gnemirko.task_manager.entity.User;
import com.gnemirko.task_manager.security.JwtTokenProvider;
import com.gnemirko.task_manager.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final JwtTokenProvider jwtTokenProvider;

  @PostMapping("/register")
  public ResponseEntity<User> register(@RequestBody AuthRequest request) {
    User user = authService.register(request);
    return ResponseEntity.ok(user);
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody AuthRequest request) {
    boolean success = authService.login(request);
    if (success) {
      String token = jwtTokenProvider.createToken(request.getEmail());
      return ResponseEntity.ok().body(token);
    } else {
      return ResponseEntity.status(401).body("Invalid credentials");
    }
  }
}
