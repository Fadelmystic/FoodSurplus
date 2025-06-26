package com.example.user_management.dto;

import com.example.user_management.enums.Role;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserCreateDTO {
    private UUID id;  // lowercase 'id'
    private String firstname;
    private String lastname;
    private String username;
    private String password;
    private String email;
    private LocalDate dateOfBirth;
    private Role role;  // Keep as enum
    private boolean accountLocked;
    private boolean enabled;
    private LocalDateTime createdDate;
    private KYCVerificationDTO kyc;
}
