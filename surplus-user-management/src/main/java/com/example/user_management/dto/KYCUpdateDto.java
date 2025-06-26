package com.example.user_management.dto;

import com.example.user_management.enums.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KYCUpdateDto {
    private String documentType;
    private String documentNumber;
    private LocalDate issuedDate;
    private LocalDate expiryDate;
    private VerificationStatus status;
}