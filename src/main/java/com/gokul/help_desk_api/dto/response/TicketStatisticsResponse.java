package com.gokul.help_desk_api.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TicketStatisticsResponse {

    private Long totalTickets;

    private Long openTickets;

    private Long inProgressTickets;

    private Long onHoldTickets;

    private Long resolvedTickets;

    private Long closedTickets;

    private Long lowPriorityTickets;

    private Long mediumPriorityTickets;

    private  Long highPriorityTickets;

    private Long criticalPriorityTickets;
}
