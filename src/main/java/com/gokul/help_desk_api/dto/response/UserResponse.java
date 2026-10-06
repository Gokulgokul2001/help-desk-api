package com.gokul.help_desk_api.dto.response;

import com.gokul.help_desk_api.entity.Role;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private String department;
    private LocalDateTime createdAt;
}