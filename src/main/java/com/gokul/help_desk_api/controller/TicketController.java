package com.gokul.help_desk_api.controller;

import com.gokul.help_desk_api.dto.request.AssignTicketRequest;
import com.gokul.help_desk_api.dto.request.CreateTicketRequest;
import com.gokul.help_desk_api.dto.request.UpdateTicketRequest;
import com.gokul.help_desk_api.dto.request.UpdateTicketStatusRequest;
import com.gokul.help_desk_api.dto.response.TicketResponse;
import com.gokul.help_desk_api.dto.response.TicketStatisticsResponse;
import com.gokul.help_desk_api.entity.Priority;
import com.gokul.help_desk_api.entity.TicketStatus;
import com.gokul.help_desk_api.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // =========================================================
    // CREATE TICKET
    // =========================================================

    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(
            @Valid @RequestBody CreateTicketRequest request,
            Authentication authentication) {

        TicketResponse response =
                ticketService.createTicket(request, authentication);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =========================================================
    // GET TICKET BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicketById(
            @PathVariable Long id,
            Authentication authentication) {

        TicketResponse response =
                ticketService.getTicketById(id, authentication);

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // GET ALL TICKETS
    // =========================================================

    @GetMapping
    public ResponseEntity<List<TicketResponse>> getAllTickets(Authentication authentication) {

        List<TicketResponse> response =
                ticketService.getAllTickets(authentication);

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // UPDATE TICKET
    // =========================================================

    @PutMapping("/{id}")
    public ResponseEntity<TicketResponse> updateTicket(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTicketRequest request,
            Authentication authentication) {

        TicketResponse response =
                ticketService.updateTicket(id, request, authentication);

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // DELETE TICKET
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(
            @PathVariable Long id) {

        ticketService.deleteTicket(id);

        return ResponseEntity.noContent().build();
    }

    // =========================================================
    // ASSIGN TICKET
    // =========================================================

    @PutMapping("/{id}/assign")
    public ResponseEntity<TicketResponse> assignTicket(
            @PathVariable Long id,
            @Valid @RequestBody AssignTicketRequest request,
            Authentication authentication) {

        TicketResponse response =
                ticketService.assignTicket(id, request, authentication);

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // UPDATE TICKET STATUS
    // =========================================================

    @PutMapping("/{id}/status")
    public ResponseEntity<TicketResponse> updateTicketStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTicketStatusRequest request,
            Authentication authentication) {

        TicketResponse response =
                ticketService.updateTicketStatus(id, request, authentication);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<TicketResponse>> getTicketsWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Long categoryId,
            Authentication authentication) {

        Page<TicketResponse> tickets =
                ticketService.getTicketsWithPagination(
                        page,
                        size,
                        keyword,
                        status,
                        priority,
                        categoryId,
                        authentication
                );

        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/statistics")
    public ResponseEntity<TicketStatisticsResponse> getTicketStatistics(
            Authentication authentication){

        TicketStatisticsResponse statistics =
                ticketService.getTicketStatistics(authentication);

        return ResponseEntity.ok(statistics);
    }
}