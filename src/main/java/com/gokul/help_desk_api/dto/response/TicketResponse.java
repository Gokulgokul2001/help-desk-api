package com.gokul.help_desk_api.dto.response;

import com.gokul.help_desk_api.entity.Priority;
import com.gokul.help_desk_api.entity.TicketStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TicketResponse {

    private Long id;

    private String ticketNumber;

    private String title;

    private String description;


    private Priority priority;

    private TicketStatus status;

    private Long categoryId;

    private String categoryName;

    private Long createdById;

    private String createdByName;

    private Long assignedToId;

    private String assignedToName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime resolvedAt;

    private LocalDateTime closedAt;
}
