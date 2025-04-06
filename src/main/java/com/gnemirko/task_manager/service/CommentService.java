package com.gnemirko.task_manager.service;

import com.gnemirko.task_manager.entity.Comment;
import com.gnemirko.task_manager.entity.Task;
import com.gnemirko.task_manager.repository.CommentRepository;
import com.gnemirko.task_manager.repository.TaskRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

  @Autowired private CommentRepository commentRepository;

  @Autowired private TaskRepository taskRepository;

  public List<Comment> getCommentsByTaskId(Long taskId) {
    Task task = taskRepository.findById(taskId).orElseThrow();
    return commentRepository.findByTask(task);
  }

  public Comment addCommentToTask(Long taskId, Comment comment) {
    Task task = taskRepository.findById(taskId).orElseThrow();
    comment.setTask(task);
    return commentRepository.save(comment);
  }
}
