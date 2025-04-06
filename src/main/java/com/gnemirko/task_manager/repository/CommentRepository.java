package com.gnemirko.task_manager.repository;

import com.gnemirko.task_manager.entity.Comment;
import com.gnemirko.task_manager.entity.Task;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
  List<Comment> findByTask(Task task);
}
