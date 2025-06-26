package com.example.user_management.repository;

import com.example.user_management.entity.KYCVerification;
import com.example.user_management.enums.VerificationStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KYCVerificationRepository extends MongoRepository<KYCVerification, String> {
    // You can add custom query methods here if needed
    Optional<KYCVerification> findByUserId(String userId);
    List<KYCVerification> findByVerified(VerificationStatus status);
}
