package com.gokul.help_desk_api.service;

import com.gokul.help_desk_api.dto.response.TicketAttachmentResponse;
import com.gokul.help_desk_api.entity.Role;
import com.gokul.help_desk_api.entity.Ticket;
import com.gokul.help_desk_api.entity.TicketAttachment;
import com.gokul.help_desk_api.entity.User;
import com.gokul.help_desk_api.exception.ResourceNotFoundException;
import com.gokul.help_desk_api.exception.UnauthorizedTicketAccessException;
import com.gokul.help_desk_api.repository.TicketAttachmentRepository;
import com.gokul.help_desk_api.repository.TicketRepository;
import com.gokul.help_desk_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketAttachmentService {

    private final TicketAttachmentRepository ticketAttachmentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;


    // =========================================================
    // Upload Attachment
    // =========================================================

    public TicketAttachmentResponse uploadAttachment(
            Long ticketId,
            MultipartFile file,
            Authentication authentication) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ticket not found"));

        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        validateTicketAccess(ticket, currentUser);

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }

        String filePath = fileStorageService.storeFile(
                file,
                ticket.getTicketNumber()
        );

        TicketAttachment attachment = TicketAttachment.builder()
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .filePath(filePath)
                .ticket(ticket)
                .uploadedBy(currentUser)
                .build();

        TicketAttachment savedAttachment =
                ticketAttachmentRepository.save(attachment);

        return mapToResponse(savedAttachment);
    }


    // =========================================================
    // Get Attachments
    // =========================================================

    public List<TicketAttachmentResponse> getAttachments(
            Long ticketId,
            Authentication authentication) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ticket not found"));

        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        validateTicketAccess(ticket, currentUser);

        return ticketAttachmentRepository
                .findByTicketIdOrderByUploadedAtAsc(ticketId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================================================
    // Validate Ticket Access
    // =========================================================

    private void validateTicketAccess(
            Ticket ticket,
            User user) {

        switch (user.getRole()) {

            case ADMIN:
                return;

            case EMPLOYEE:

                if (!ticket.getCreatedBy()
                        .getId()
                        .equals(user.getId())) {

                    throw new UnauthorizedTicketAccessException(
                            "You are not authorized to access this ticket"
                    );
                }

                return;

            case IT_SUPPORT:

                if (ticket.getAssignedTo() == null ||
                        !ticket.getAssignedTo()
                                .getId()
                                .equals(user.getId())) {

                    throw new UnauthorizedTicketAccessException(
                            "You are not authorized to access this ticket"
                    );
                }

                return;

            default:

                throw new UnauthorizedTicketAccessException(
                        "You are not authorized to access this ticket"
                );
        }
    }


    // =========================================================
    // Get Single Attachment
    // =========================================================

    public TicketAttachment getAttachment(
            Long ticketId,
            Long attachmentId,
            Authentication authentication) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ticket not found"));

        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        validateTicketAccess(ticket, currentUser);

        TicketAttachment attachment =
                ticketAttachmentRepository.findById(attachmentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Attachment not found"
                                ));

        if (!attachment.getTicket()
                .getId()
                .equals(ticketId)) {

            throw new ResourceNotFoundException(
                    "Attachment does not belong to this ticket"
            );
        }

        return attachment;
    }


    // =========================================================
    // Load Attachment File
    // =========================================================

    public byte[] loadAttachmentFile(
            TicketAttachment attachment) {

        return fileStorageService.loadFile(
                attachment.getFilePath()
        );
    }


    // =========================================================
    // Delete Attachment
    // =========================================================

    public void deleteAttachment(
            Long ticketId,
            Long attachmentId,
            Authentication authentication) {

        // Find ticket
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Ticket not found"
                        ));

        // Find current logged-in user
        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        ));

        // Find attachment
        TicketAttachment attachment =
                ticketAttachmentRepository.findById(attachmentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Attachment not found"
                                ));

        // Make sure attachment belongs to this ticket
        if (!attachment.getTicket()
                .getId()
                .equals(ticketId)) {

            throw new ResourceNotFoundException(
                    "Attachment does not belong to this ticket"
            );
        }

        // Verify user has access to the ticket
        validateTicketAccess(ticket, currentUser);

        // Only ADMIN or the user who uploaded
        // the attachment can delete it
        if (currentUser.getRole() != Role.ADMIN &&
                !attachment.getUploadedBy()
                        .getId()
                        .equals(currentUser.getId())) {

            throw new UnauthorizedTicketAccessException(
                    "You are not authorized to delete this attachment"
            );
        }

        // Delete physical file
        fileStorageService.deleteFile(
                attachment.getFilePath()
        );

        // Delete database record
        ticketAttachmentRepository.delete(attachment);
    }


    // =========================================================
    // Map Entity to Response
    // =========================================================

    private TicketAttachmentResponse mapToResponse(
            TicketAttachment attachment) {

        return TicketAttachmentResponse.builder()
                .id(attachment.getId())
                .fileName(attachment.getFileName())
                .fileType(attachment.getFileType())
                .fileSize(attachment.getFileSize())
                .ticketId(attachment.getTicket().getId())
                .uploadedById(attachment.getUploadedBy().getId())
                .uploadedByName(attachment.getUploadedBy().getName())
                .uploadedAt(attachment.getUploadedAt())
                .build();
    }
}