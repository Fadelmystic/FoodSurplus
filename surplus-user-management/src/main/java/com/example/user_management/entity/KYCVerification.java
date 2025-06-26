package com.example.user_management.entity;

import com.example.user_management.enums.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Document(collection = "kyc_verifications")

public class KYCVerification {
    @Id
    private String id;

    private UUID userId;
    private String documentType;
    private String documentNumber;
    private LocalDate issuedDate;
    private LocalDate expiryDate;
    private VerificationStatus verified;
}