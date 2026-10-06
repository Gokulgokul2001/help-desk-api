package com.gokul.help_desk_api.specification;

import com.gokul.help_desk_api.entity.Priority;
import com.gokul.help_desk_api.entity.Ticket;
import com.gokul.help_desk_api.entity.TicketStatus;
import org.springframework.data.jpa.domain.Specification;

public class TicketSpecification {

    public static Specification<Ticket> hasStatus(TicketStatus status) {
        return (root, query, criteriaBuilder) ->
                status == null
                        ? null
                        : criteriaBuilder.equal(
                        root.get("status"),
                        status
                );
    }

    public static Specification<Ticket> hasPriority(Priority priority) {
        return (root, query, criteriaBuilder) ->
                priority == null
                        ? null
                        : criteriaBuilder.equal(
                        root.get("priority"),
                        priority
                );
    }

    public static Specification<Ticket> hasCategory(Long categoryId) {
        return (root, query, criteriaBuilder) ->
                categoryId == null
                        ? null
                        : criteriaBuilder.equal(
                        root.get("category").get("id"),
                        categoryId
                );
    }

    public static Specification<Ticket> createdBy(Long userId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("createdBy").get("id"),
                        userId
                );
    }

    public static Specification<Ticket> assignedTo(Long userId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("assignedTo").get("id"),
                        userId
                );
    }

    public static Specification<Ticket> hasKeyword(String keyword){
        return (root, query, criteriaBuilder) -> {

            if (keyword == null || keyword.trim().isEmpty()) {
                return null;
            }

            String searchKeyword = "%" + keyword.trim().toLowerCase() + "%";

            return criteriaBuilder.or(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("ticketNumber")),
                            searchKeyword
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("ticket")),
                            searchKeyword
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("description")),
                            searchKeyword
                    )
            );
        };
    }
}