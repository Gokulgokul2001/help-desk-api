package com.gokul.help_desk_api.dto.response;


import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.parameters.P;

import java.time.LocalDateTime;

@Getter
@Builder
public class TicketAttachmentResponse {

    private Long id;

    private String fileName;

    private String fileType;

    private Long fileSize;

    private Long ticketId;

    private Long uploadedById;

    private String uploadedByName;

    private LocalDateTime uploadedAt;
}
