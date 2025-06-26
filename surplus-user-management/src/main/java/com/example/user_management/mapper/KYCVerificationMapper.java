package com.example.user_management.mapper;

import com.example.user_management.dto.KYCVerificationDTO;
import com.example.user_management.entity.KYCVerification;
import com.example.user_management.enums.VerificationStatus;
import org.springframework.stereotype.Component;

@Component
public class KYCVerificationMapper {

    // Convert KYCVerification entity to KYCVerificationDTO
    public KYCVerificationDTO toKYCVerificationDTO(KYCVerification kyc) {
        if (kyc == null) {
            return null;
        }

        KYCVerificationDTO kycVerificationDTO = new KYCVerificationDTO();
        kycVerificationDTO.setDocumentType(kyc.getDocumentType());
        kycVerificationDTO.setDocumentNumber(kyc.getDocumentNumber());
        kycVerificationDTO.setIssuedDate(kyc.getIssuedDate());
        kycVerificationDTO.setExpiryDate(kyc.getExpiryDate());
        kycVerificationDTO.setVerified(kyc.getVerified().toString());  // Enum to String if needed

        return kycVerificationDTO;
    }

    // Convert KYCVerificationDTO to KYCVerification entity (if needed for saving back to DB)
    public KYCVerification toKYCVerification(KYCVerificationDTO kycDTO) {
        if (kycDTO == null) {
            return null;
        }

        KYCVerification kycVerification = new KYCVerification();
        kycVerification.setDocumentType(kycDTO.getDocumentType());
        kycVerification.setDocumentNumber(kycDTO.getDocumentNumber());
        kycVerification.setIssuedDate(kycDTO.getIssuedDate());
        kycVerification.setExpiryDate(kycDTO.getExpiryDate());
        kycVerification.setVerified(VerificationStatus.valueOf(kycDTO.getVerified()));  // String to Enum

        return kycVerification;
    }
}
