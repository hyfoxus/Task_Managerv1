package com.gnemirko.task_manager.controller;

import com.gnemirko.task_manager.entity.Comment;
import com.gnemirko.task_manager.repository.CommentRepository;
import com.gnemirko.task_manager.service.CommentService;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
public class CommentController {

  @Autowired private CommentService commentService;
  @Autowired
  private CommentRepository commentRepository;


  @GetMapping("/task/{taskId}")
  public List<Comment> getCommentsByTask(@PathVariable Long taskId) {
    return commentService.getCommentsByTaskId(taskId);
  }

  @PostMapping("/task/{taskId}")
  public Comment addCommentToTask(@PathVariable Long taskId, @RequestBody Comment comment) {
    return commentService.addCommentToTask(taskId, comment);
  }

  @DeleteMapping("/{commentId}")
  @PreAuthorize("hasRole('ADMIN') or @commentSecurity.isAuthor(authentication, #commentId)")
  public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
    commentService.deleteComment(commentId);
    return ResponseEntity.ok("Comment deleted");
  }
}
