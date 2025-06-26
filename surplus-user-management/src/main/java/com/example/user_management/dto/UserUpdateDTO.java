package com.example.user_management.dto;

import com.example.user_management.enums.Role;
import lombok.Data;

import java.time.LocalDate;

@Data

public class UserUpdateDTO {
    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private Boolean enabled;
    private Boolean accountLocked;
    private Role role;
}
