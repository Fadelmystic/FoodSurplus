package com.example.user_management.service;

import com.example.user_management.entity.KYCVerification;
import com.example.user_management.entity.User;
import com.example.user_management.enums.VerificationStatus;
import com.example.user_management.repository.KYCVerificationRepository;
import com.example.user_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KYCService {

    private final KYCVerificationRepository kycRepository;
    private final UserRepository userRepository;

    @Autowired
    public KYCService(KYCVerificationRepository kycRepository, UserRepository userRepository) {
        this.kycRepository = kycRepository;
        this.userRepository = userRepository;
    }

    /**
     * Find KYC verification by user ID
     */
    public Optional<KYCVerification> findKycByUserId(String userId) {
        return kycRepository.findByUserId(userId);
    }

    /**
     * Find KYC verifications by status
     */
    public List<KYCVerification> findKycByStatus(VerificationStatus status) {
        return kycRepository.findByVerified(status);
    }

    /**
     * Submit a new KYC verification
     */
    public KYCVerification submitKyc(KYCVerification kycVerification) {
        // Set status to PENDING for new submissions
        kycVerification.setVerified(VerificationStatus.PENDING);

        // Save the KYC verification
        KYCVerification savedKyc = kycRepository.save(kycVerification);

        // Update the user's KYC reference
        Optional<User> optionalUser = userRepository.findById(kycVerification.getUserId());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setKyc(savedKyc);
            userRepository.save(user);
        }

        return savedKyc;
    }

    /**
     * Update an existing KYC verification
     */
    public Optional<KYCVerification> updateKyc(String id, KYCVerification updatedKyc) {
        Optional<KYCVerification> existingKyc = kycRepository.findById(id);

        if (existingKyc.isPresent()) {
            KYCVerification kyc = existingKyc.get();

            // Update fields if provided
            if (updatedKyc.getDocumentType() != null) {
                kyc.setDocumentType(updatedKyc.getDocumentType());
            }

            if (updatedKyc.getDocumentNumber() != null) {
                kyc.setDocumentNumber(updatedKyc.getDocumentNumber());
            }

            if (updatedKyc.getIssuedDate() != null) {
                kyc.setIssuedDate(updatedKyc.getIssuedDate());
            }

            if (updatedKyc.getExpiryDate() != null) {
                kyc.setExpiryDate(updatedKyc.getExpiryDate());
            }

            if (updatedKyc.getVerified() != null) {
                kyc.setVerified(updatedKyc.getVerified());
            }

            return Optional.of(kycRepository.save(kyc));
        }

        return Optional.empty();
    }

    /**
     * Update the verification status of a KYC
     */
    public Optional<KYCVerification> updateKycStatus(String id, VerificationStatus status) {
        Optional<KYCVerification> existingKyc = kycRepository.findById(id);

        if (existingKyc.isPresent()) {
            KYCVerification kyc = existingKyc.get();
            kyc.setVerified(status);
            return Optional.of(kycRepository.save(kyc));
        }

        return Optional.empty();
    }
}
