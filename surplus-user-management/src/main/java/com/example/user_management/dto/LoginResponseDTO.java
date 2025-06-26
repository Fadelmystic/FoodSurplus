package com.example.user_management.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    private String access_token;
    private String refresh_token;
    private String token_type;
    private Integer expires_in;
    private String user_id;
    private String username;
    private String email;
    private String role;
    private String firstname;
    private String lastname;
} 