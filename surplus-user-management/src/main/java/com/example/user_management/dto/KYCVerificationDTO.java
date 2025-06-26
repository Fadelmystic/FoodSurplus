package com.example.user_management.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class KYCVerificationDTO {
    private String documentType;
    private String documentNumber;
    private LocalDate issuedDate;
    private LocalDate expiryDate;
    private String verified;  // String or Enum to represent verification status
}

