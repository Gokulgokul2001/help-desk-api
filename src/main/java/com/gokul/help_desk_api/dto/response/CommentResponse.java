package com.gokul.help_desk_api.dto.response;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponse {
    private Long id;

    private String message;

    private Long ticketId;

    private Long createdById;

    private String createdByName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
