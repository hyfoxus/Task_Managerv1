package com.example.task_manager.controller;

import com.example.task_manager.entity.Comment;
import com.example.task_manager.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/task/{taskId}")
    public List<Comment> getCommentsByTask(@PathVariable Long taskId) {
        return commentService.getCommentsByTaskId(taskId);
    }

    @PostMapping("/task/{taskId}")
    public Comment addCommentToTask(@PathVariable Long taskId, @RequestBody Comment comment) {
        return commentService.addCommentToTask(taskId, comment);
    }
}