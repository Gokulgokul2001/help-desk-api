package com.gokul.help_desk_api.service;

import com.gokul.help_desk_api.dto.request.CreateCommentRequest;
import com.gokul.help_desk_api.dto.request.UpdateCommentRequest;
import com.gokul.help_desk_api.dto.request.UpdateTicketRequest;
import com.gokul.help_desk_api.dto.response.CommentResponse;
import com.gokul.help_desk_api.entity.Comment;
import com.gokul.help_desk_api.entity.Ticket;
import com.gokul.help_desk_api.entity.User;
import com.gokul.help_desk_api.exception.UnauthorizedTicketAccessException;
import com.gokul.help_desk_api.repository.CommentRepository;
import com.gokul.help_desk_api.repository.TicketRepository;
import com.gokul.help_desk_api.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository,
                          TicketRepository ticketRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
    }

    public CommentResponse createComment(
            Long ticketId,
            CreateCommentRequest request,
            Authentication authentication) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() ->
                        new RuntimeException("Ticket not found"));

        User currentUser = userRepository.findByEmail(
                        authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        validateTicketAccess(ticket, currentUser);

        Comment comment = Comment.builder()
                .message(request.getMessage())
                .ticket(ticket)
                .createdBy(currentUser)
                .build();

        Comment savedComment = commentRepository.save(comment);

        return mapToResponse(savedComment);
    }

    public List<CommentResponse> getComments(
            Long ticketId,
            Authentication authentication) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        User currentUser = userRepository.findByEmail(
                        authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        validateTicketAccess(ticket, currentUser);

        return commentRepository
                .findByTicketIdOrderByCreatedAtAsc(ticketId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private void validateTicketAccess(
            Ticket ticket,
            User currentUser) {

        if (currentUser.getRole().name().equals("ADMIN")) {
            return;
        }

        if (currentUser.getRole().name().equals("EMPLOYEE")) {

            if (ticket.getCreatedBy().getId()
                    .equals(currentUser.getId())) {
                return;
            }
        }

        if (currentUser.getRole().name().equals("IT_SUPPORT")) {

            if (ticket.getAssignedTo() != null &&
                    ticket.getAssignedTo().getId()
                            .equals(currentUser.getId())) {
                return;
            }
        }
        throw new UnauthorizedTicketAccessException(
                "you are not authorized to access this ticket");
    }

    public CommentResponse updateComment(
            Long commentId,
            UpdateCommentRequest request,
            Authentication authentication) {

        // Find comment
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() ->
                        new RuntimeException("Comment not found"));

        // Find current user
        User currentUser = userRepository.findByEmail(
                        authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // ADMIN can update any comment
        if (currentUser.getRole().name().equals("ADMIN")) {
            // Allowed
        }
        // Comment author can update their own comment
        else if (!comment.getCreatedBy().getId()
                .equals(currentUser.getId())) {

            throw new UnauthorizedTicketAccessException(
                    "You are not authorized to update this comment");
        }

        // Update message
        comment.setMessage(request.getMessage());

        Comment updatedComment = commentRepository.save(comment);

        return mapToResponse(updatedComment);
    }

    public void deleteComment(
            Long commentId,
            Authentication authentication) {

        // Find comment
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() ->
                        new RuntimeException("Comment not found"));

        // Find current user
        User currentUser = userRepository.findByEmail(
                        authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // ADMIN can delete any comment
        if (currentUser.getRole().name().equals("ADMIN")) {
            // Allowed
        }
        // Comment author can delete their own comment
        else if (!comment.getCreatedBy().getId()
                .equals(currentUser.getId())) {

            throw new UnauthorizedTicketAccessException(
                    "You are not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }

    private CommentResponse mapToResponse(Comment comment){

        return CommentResponse.builder()
                .id(comment.getId())
                .message(comment.getMessage())
                .ticketId(comment.getTicket().getId())
                .createdById(comment.getCreatedBy().getId())
                .createdByName(comment.getCreatedBy().getName())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}

