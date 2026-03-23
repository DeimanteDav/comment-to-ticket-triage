package com.deimante.pulsedesk.controller;

import com.deimante.pulsedesk.model.Comment;
import com.deimante.pulsedesk.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public Comment submitComment(@RequestBody Comment comment) {
        return commentService.processComment(comment.getText());
    }

    @GetMapping
    public List<Comment> getComments() {
        return commentService.getAllComments();
    }
}
