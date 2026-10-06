package com.gokul.help_desk_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCommentRequest {

    @NotBlank(message = "Comment message is required")
    @Size(max = 2000, message = "Comment message cannot exceed 2000 characters")
    private String message;
}
