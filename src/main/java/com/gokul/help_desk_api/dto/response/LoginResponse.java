package com.gokul.help_desk_api.dto.response;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private String token;
    private Long userId;
    private String name;
    private String email;
    private String role;

}