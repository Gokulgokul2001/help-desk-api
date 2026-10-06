package com.gokul.help_desk_api.controller;

import com.gokul.help_desk_api.dto.response.CommentResponse;
import com.gokul.help_desk_api.dto.request.CreateCommentRequest;
import com.gokul.help_desk_api.dto.request.UpdateCommentRequest;
import com.gokul.help_desk_api.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/tickets/{ticketId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long ticketId,
            @Valid @RequestBody CreateCommentRequest request,
            Authentication authentication) {

        CommentResponse response = commentService.createComment(
                ticketId,
                request,
                authentication
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/tickets/{ticketId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable Long ticketId,
            Authentication authentication) {

        List<CommentResponse> comments =
                commentService.getComments(
                        ticketId,
                        authentication
                );

        return ResponseEntity.ok(comments);
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentRequest request,
            Authentication authentication) {

        CommentResponse response = commentService.updateComment(
                commentId,
                request,
                authentication
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            Authentication authentication) {

        commentService.deleteComment(
                commentId,
                authentication
        );

        return ResponseEntity.noContent().build();
    }
}