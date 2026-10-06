package com.gokul.help_desk_api.controller;

import com.gokul.help_desk_api.dto.response.TicketAttachmentResponse;
import com.gokul.help_desk_api.service.TicketAttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.gokul.help_desk_api.entity.TicketAttachment;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TicketAttachmentController {

    private final TicketAttachmentService ticketAttachmentService;

    @PostMapping("/api/tickets/{ticketId}/attachments")
    public ResponseEntity<TicketAttachmentResponse> uploadAttachment(
            @PathVariable Long ticketId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        TicketAttachmentResponse response =
                ticketAttachmentService.uploadAttachment(
                        ticketId,
                        file,
                        authentication
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/tickets/{ticketId}/attachments")
    public ResponseEntity<List<TicketAttachmentResponse>> getAttachments(
            @PathVariable Long ticketId,
            Authentication authentication) {

        return ResponseEntity.ok(
                ticketAttachmentService.getAttachments(
                        ticketId,
                        authentication
                )
        );
    }
    @GetMapping("/api/tickets/{ticketId}/attachments/{attachmentId}")
    public ResponseEntity<byte[]> downloadAttachment(
            @PathVariable Long ticketId,
            @PathVariable Long attachmentId,
            Authentication authentication) {

        TicketAttachment attachment =
                ticketAttachmentService.getAttachment(
                        ticketId,
                        attachmentId,
                        authentication
                );

        byte[] fileData =
                ticketAttachmentService.loadAttachmentFile(attachment);

        return ResponseEntity.ok()
                .header(
                        "Content-Disposition",
                        "attachment; filename=\"" +
                                attachment.getFileName() +
                                "\""
                )
                .header(
                        "Content-Type",
                        attachment.getFileType()
                )
                .body(fileData);
    }

    @DeleteMapping("/api/tickets/{ticketId}/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(
            @PathVariable Long ticketId,
            @PathVariable Long attachmentId,
            Authentication authentication) {

        ticketAttachmentService.deleteAttachment(
                ticketId,
                attachmentId,
                authentication
        );

        return ResponseEntity.noContent().build();
    }
}