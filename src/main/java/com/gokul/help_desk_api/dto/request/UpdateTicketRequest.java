package com.gokul.help_desk_api.dto.request;

import com.gokul.help_desk_api.entity.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTicketRequest {

    @NotBlank(message = "Title is required")
    @Size(
            min = 5,
            max = 255,
            message = "Title must be between 5 and 255 characters"
    )
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotNull(message = "Priority is required")
    private Priority priority;
}