package com.gokul.help_desk_api.service;

import com.gokul.help_desk_api.dto.request.AssignTicketRequest;
import com.gokul.help_desk_api.dto.request.CreateTicketRequest;
import com.gokul.help_desk_api.dto.request.UpdateTicketRequest;
import com.gokul.help_desk_api.dto.request.UpdateTicketStatusRequest;
import com.gokul.help_desk_api.dto.response.TicketResponse;
import com.gokul.help_desk_api.dto.response.TicketStatisticsResponse;
import com.gokul.help_desk_api.entity.Priority;
import com.gokul.help_desk_api.entity.Category;
import com.gokul.help_desk_api.entity.Ticket;
import com.gokul.help_desk_api.entity.Role;
import com.gokul.help_desk_api.entity.TicketStatus;
import com.gokul.help_desk_api.entity.User;
import com.gokul.help_desk_api.exception.InvalidTicketStatusException;
import com.gokul.help_desk_api.exception.UnauthorizedTicketAccessException;
import com.gokul.help_desk_api.specification.TicketSpecification;
import com.gokul.help_desk_api.repository.CategoryRepository;
import com.gokul.help_desk_api.repository.TicketRepository;
import com.gokul.help_desk_api.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public TicketService(
            TicketRepository ticketRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository) {

        this.ticketRepository = ticketRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    // =========================================================
    // CREATE TICKET
    // =========================================================

    public TicketResponse createTicket(
            CreateTicketRequest request,
            Authentication authentication) {

        // Get logged-in user's email from JWT
        String email = authentication.getName();

        // Find the logged-in user
        User createdBy = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // Find the selected category
        Category category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));

        // Create ticket with temporary ticket number
        Ticket ticket = Ticket.builder()
                .ticketNumber("TEMP-" + System.currentTimeMillis())
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .category(category)
                .createdBy(createdBy)
                .build();

        // First save - generates database ID
        Ticket savedTicket = ticketRepository.save(ticket);

        // Generate final ticket number
        savedTicket.setTicketNumber(
                String.format("IT-%06d", savedTicket.getId())
        );

        // Save again with final ticket number
        savedTicket = ticketRepository.save(savedTicket);

        return mapToResponse(savedTicket);
    }

    // =========================================================
    // GET TICKET BY ID
    // =========================================================

    public TicketResponse getTicketById(
            Long id,
            Authentication authentication) {

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Ticket not found"));

        User currentUser = userRepository.findByEmail(
                        authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // ADMIN can view any ticket
        if (currentUser.getRole() == Role.ADMIN) {
            return mapToResponse(ticket);
        }

        // EMPLOYEE can view only tickets created by them
        if (currentUser.getRole() == Role.EMPLOYEE) {

            if (!ticket.getCreatedBy().getId()
                    .equals(currentUser.getId())) {

                throw new UnauthorizedTicketAccessException(
                        "You are not authorized to view this ticket");
            }

            return mapToResponse(ticket);
        }

        // IT_SUPPORT can view tickets assigned to them
        if (currentUser.getRole() == Role.IT_SUPPORT) {

            if (ticket.getAssignedTo() == null ||
                    !ticket.getAssignedTo().getId()
                            .equals(currentUser.getId())) {

                throw new RuntimeException(
                        "You are not authorized to view this ticket");
            }

            return mapToResponse(ticket);
        }

        throw new RuntimeException(
                "You are not authorized to view this ticket");
    }

    // =========================================================
    // GET ALL TICKETS
    // =========================================================

    public List<TicketResponse> getAllTickets(
            Authentication authentication) {

        User currentUser = userRepository.findByEmail(
                        authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        List<Ticket> tickets;

        // ADMIN can see all tickets
        if (currentUser.getRole() == Role.ADMIN) {

            tickets = ticketRepository.findAll();

        }
        // EMPLOYEE can see only tickets created by them
        else if (currentUser.getRole() == Role.EMPLOYEE) {

            tickets = ticketRepository.findAll()
                    .stream()
                    .filter(ticket ->
                            ticket.getCreatedBy().getId()
                                    .equals(currentUser.getId()))
                    .toList();

        }
        // IT_SUPPORT can see only tickets assigned to them
        else if (currentUser.getRole() == Role.IT_SUPPORT) {

            tickets = ticketRepository.findAll()
                    .stream()
                    .filter(ticket ->
                            ticket.getAssignedTo() != null
                                    && ticket.getAssignedTo().getId()
                                    .equals(currentUser.getId()))
                    .toList();

        }
        else {
            throw new UnauthorizedTicketAccessException(
                    "You are not authorized to view tickets");
        }

        return tickets.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public Page<TicketResponse> getTicketsWithPagination(
            int page,
            int size,
            String keyword,
            TicketStatus status,
            Priority priority,
            Long categoryId,
            Authentication authentication) {

        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page,
                size,
                Sort.by(Sort.Direction.DESC,"createdAt"));

        Specification<Ticket> specification =
                (root, query, criteriaBuilder) -> null;

        // Status filter
        if (status != null) {
            specification = specification.and(
                    TicketSpecification.hasStatus(status)
            );
        }

        // Priority filter
        if (priority != null) {
            specification = specification.and(
                    TicketSpecification.hasPriority(priority)
            );
        }

        // Category filter
        if (categoryId != null) {
            specification = specification.and(
                    TicketSpecification.hasCategory(categoryId)
            );
        }

        // Role-based ticket visibility
        if (currentUser.getRole() == Role.ADMIN) {

            // ADMIN can see all tickets

        } else if (currentUser.getRole() == Role.EMPLOYEE) {

            specification = specification.and(
                    TicketSpecification.createdBy(currentUser.getId())
            );

        } else if (currentUser.getRole() == Role.IT_SUPPORT) {

            specification = specification.and(
                    TicketSpecification.assignedTo(currentUser.getId())
            );

        } else {

            throw new UnauthorizedTicketAccessException(
                    "You are not authorized to view tickets"
            );
        }

        Page<Ticket> tickets =
                ticketRepository.findAll(specification, pageable);

        return tickets.map(this::mapToResponse);
    }
    // =========================================================
    // UPDATE TICKET
    // =========================================================

    public TicketResponse updateTicket(
            Long id,
            UpdateTicketRequest request,
            Authentication authentication) {

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Ticket not found"));

        User currentUser = userRepository.findByEmail(
                        authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // ADMIN can update any ticket
        if (currentUser.getRole() == Role.ADMIN) {
            // Allowed
        }

        // EMPLOYEE can update only tickets created by them
        else if (currentUser.getRole() == Role.EMPLOYEE) {

            if (!ticket.getCreatedBy().getId()
                    .equals(currentUser.getId())) {

                throw new UnauthorizedTicketAccessException(
                        "You are not authorized to update this ticket");
            }
        }

        // IT_SUPPORT can update only tickets assigned to them
        else if (currentUser.getRole() == Role.IT_SUPPORT) {

            if (ticket.getAssignedTo() == null ||
                    !ticket.getAssignedTo().getId()
                            .equals(currentUser.getId())) {

                throw new UnauthorizedTicketAccessException(
                        "You are not authorized to update this ticket");
            }
        }

        else {
            throw new UnauthorizedTicketAccessException(
                    "You are not authorized to update this ticket");
        }

        Category category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));

        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());
        ticket.setCategory(category);

        Ticket updatedTicket = ticketRepository.save(ticket);

        return mapToResponse(updatedTicket);
    }
    // =========================================================
    // DELETE TICKET
    // =========================================================

    public void deleteTicket(Long id) {

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Ticket not found"));

        ticketRepository.delete(ticket);
    }

    // =========================================================
    // ASSIGN TICKET
    // =========================================================

    public TicketResponse assignTicket(
            Long id,
            AssignTicketRequest request,
            Authentication authentication) {

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Ticket not found"));

        User currentUser = userRepository.findByEmail(
                        authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // Only ADMIN and IT_SUPPORT can assign tickets
        if (currentUser.getRole() != Role.ADMIN
                && currentUser.getRole() != Role.IT_SUPPORT) {

            throw new UnauthorizedTicketAccessException(
                    "You are not authorized to assign tickets");
        }

        // Find the user who will be assigned
        User assignedUser = userRepository.findById(
                        request.getAssignedToId())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // The assigned user must be IT_SUPPORT
        if (assignedUser.getRole() != Role.IT_SUPPORT) {

            throw new RuntimeException(
                    "Ticket can only be assigned to IT Support users");
        }

        ticket.setAssignedTo(assignedUser);

        Ticket updatedTicket = ticketRepository.save(ticket);

        return mapToResponse(updatedTicket);
    }

    // =========================================================
    // UPDATE TICKET STATUS
    // =========================================================

    public TicketResponse updateTicketStatus(
            Long id,
            UpdateTicketStatusRequest request,
            Authentication authentication) {

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Ticket not found"));

        User currentUser = userRepository.findByEmail(
                authentication.getName())
                .orElseThrow(()-> new RuntimeException("User not found"));

        if (currentUser.getRole() == Role.ADMIN){
            
        } else if (currentUser.getRole() == Role.EMPLOYEE) {
            throw new UnauthorizedTicketAccessException(
                    "Employees are not authorized to change ticket status");
        } else if (currentUser.getRole() == Role.IT_SUPPORT) {

            if (ticket.getAssignedTo() == null ||
            !ticket.getAssignedTo().getId()
                    .equals(currentUser.getId())){
                throw new UnauthorizedTicketAccessException(
                        "You are not authorized to change the status of this ticket");
            }
        }
        else {
            throw new UnauthorizedTicketAccessException(
                    "You are not authorized to change ticket status");
        }

        TicketStatus currentStatus = ticket.getStatus();
        TicketStatus newStatus = request.getStatus();

        // Validate status transition
        if (!isValidStatusTransition(currentStatus, newStatus)) {

            throw new InvalidTicketStatusException(
                    "Cannot change ticket status from "
                            + currentStatus
                            + " to "
                            + newStatus
            );
        }

        // Update status
        ticket.setStatus(newStatus);

        // Set resolved time
        if (newStatus == TicketStatus.RESOLVED
                && ticket.getResolvedAt() == null) {

            ticket.setResolvedAt(LocalDateTime.now());
        }

        // Set closed time
        if (newStatus == TicketStatus.CLOSED
                && ticket.getClosedAt() == null) {

            ticket.setClosedAt(LocalDateTime.now());
        }

        Ticket updatedTicket = ticketRepository.save(ticket);

        return mapToResponse(updatedTicket);
    }

    // =========================================================
    // VALIDATE STATUS TRANSITION
    // =========================================================

    private boolean isValidStatusTransition(
            TicketStatus currentStatus,
            TicketStatus newStatus) {

        return switch (currentStatus) {

            case OPEN ->
                    newStatus == TicketStatus.IN_PROGRESS;

            case IN_PROGRESS ->
                    newStatus == TicketStatus.ON_HOLD
                            || newStatus == TicketStatus.RESOLVED;

            case ON_HOLD ->
                    newStatus == TicketStatus.IN_PROGRESS
                            || newStatus == TicketStatus.RESOLVED;

            case RESOLVED ->
                    newStatus == TicketStatus.CLOSED;

            case CLOSED ->
                    false;
        };
    }

    public TicketStatisticsResponse getTicketStatistics(
            Authentication authentication){

        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(()-> new RuntimeException("User not found"));

        List<Ticket> tickets;

        if (currentUser.getRole() == Role.ADMIN){
            tickets = ticketRepository.findAll();
        } else if (currentUser.getRole() == Role.EMPLOYEE) {

            tickets = ticketRepository.findAll()
                    .stream()
                    .filter(ticket ->
                            ticket.getCreatedBy().getId()
                                    .equals(currentUser.getId()))
                    .toList();
        } else if (currentUser.getRole() == Role.IT_SUPPORT) {

            tickets = ticketRepository.findAll()
                    .stream()
                    .filter(ticket ->
                            ticket.getAssignedTo() != null &&
                            ticket.getAssignedTo().getId()
                                    .equals(currentUser.getId()))
                    .toList();
        } else {
            throw new UnauthorizedTicketAccessException(
                    "You are not authorized to view ticket statistics");
        }

        long totalTickets = tickets.size();

        long openTickets = tickets.stream()
                .filter(ticket -> ticket.getStatus() == TicketStatus.OPEN)
                .count();

        long inProgressTickets = tickets.stream()
                .filter(ticket -> ticket.getStatus() == TicketStatus.IN_PROGRESS)
                .count();

        long onHoldTickets = tickets.stream()
                .filter(ticket -> ticket.getStatus() == TicketStatus.ON_HOLD)
                .count();

        long resolvedTickets = tickets.stream()
                .filter(ticket -> ticket.getStatus() == TicketStatus.RESOLVED)
                .count();

        long closedTickets = tickets.stream()
                .filter(ticket -> ticket.getStatus() == TicketStatus.CLOSED)
                .count();

        long lowPriorityTickets = tickets.stream()
                .filter(ticket -> ticket.getPriority() == Priority.LOW)
                .count();

        long mediumPriorityTickets = tickets.stream()
                .filter(ticket -> ticket.getPriority() == Priority.MEDIUM)
                .count();

        long highPriorityTickets = tickets.stream()
                .filter(ticket -> ticket.getPriority() == Priority.HIGH)
                .count();

        long criticalPriorityTickets = tickets.stream()
                .filter(ticket -> ticket.getPriority() == Priority.CRITICAL)
                .count();

        return TicketStatisticsResponse.builder()
                .totalTickets(totalTickets)
                .openTickets(openTickets)
                .inProgressTickets(inProgressTickets)
                .onHoldTickets(onHoldTickets)
                .resolvedTickets(resolvedTickets)
                .closedTickets(closedTickets)
                .lowPriorityTickets(lowPriorityTickets)
                .mediumPriorityTickets(mediumPriorityTickets)
                .highPriorityTickets(highPriorityTickets)
                .criticalPriorityTickets(criticalPriorityTickets)
                .build();


    }

    // =========================================================
    // MAP ENTITY TO RESPONSE
    // =========================================================

    private TicketResponse mapToResponse(Ticket ticket) {

        return TicketResponse.builder()
                .id(ticket.getId())
                .ticketNumber(ticket.getTicketNumber())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .priority(ticket.getPriority())
                .status(ticket.getStatus())

                .categoryId(ticket.getCategory().getId())
                .categoryName(ticket.getCategory().getName())

                .createdById(ticket.getCreatedBy().getId())
                .createdByName(ticket.getCreatedBy().getName())

                .assignedToId(
                        ticket.getAssignedTo() != null
                                ? ticket.getAssignedTo().getId()
                                : null
                )

                .assignedToName(
                        ticket.getAssignedTo() != null
                                ? ticket.getAssignedTo().getName()
                                : null
                )

                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .resolvedAt(ticket.getResolvedAt())
                .closedAt(ticket.getClosedAt())

                .build();
    }
}