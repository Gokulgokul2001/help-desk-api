package com.gokul.help_desk_api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignTicketRequest {

    @NotNull(message = "IT Support user ID is required")
    private Long assignedToId;
}
