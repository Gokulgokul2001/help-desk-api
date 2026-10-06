package com.gokul.help_desk_api.dto.request;


import com.gokul.help_desk_api.entity.TicketStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTicketStatusRequest {
    @NotNull(message = "Ticket status is required")
    private TicketStatus status;
}
