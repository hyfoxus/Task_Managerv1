package com.gnemirko.task_manager.security;

import com.gnemirko.task_manager.entity.Comment;
import com.gnemirko.task_manager.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentSecurity {

  private final CommentRepository commentRepository;

  public boolean isAuthor(Authentication authentication, Long commentId) {
    String email = authentication.getName();
    Comment comment = commentRepository.findById(commentId).orElse(null);
    return comment != null
        && comment.getAuthor() != null
        && comment.getAuthor().getEmail().equals(email);
  }
}
