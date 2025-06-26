package com.example.user_management.entity;

import com.example.user_management.enums.Role;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Document(collection = "userscollection")

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString

public class User {
    @Id
    private String id; // Same as Keycloak ID

    private String firstname;
    private String lastname;
    private LocalDate dateOfBirth;
    private String email;
    private String username;
    private String city;

    private boolean accountLocked;
    private boolean enabled;
    private LocalDateTime createdDate;
    private Role role;
    @DBRef
    private KYCVerification kyc;
}
