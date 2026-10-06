package com.gokul.help_desk_api.repository;

import com.gokul.help_desk_api.entity.TicketAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketAttachmentRepository extends
        JpaRepository<TicketAttachment, Long> {

    List<TicketAttachment> findByTicketIdOrderByUploadedAtAsc(Long ticketId);
}
