package com.example.user_management.enums;

public enum VerificationStatus {
    PENDING, // KYC is still under review
    APPROVED, // KYC has been approved
    REJECTED, // KYC was rejected
    EXPIRED // KYC verification expired
}