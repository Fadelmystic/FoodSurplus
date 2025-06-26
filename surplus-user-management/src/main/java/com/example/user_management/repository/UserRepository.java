package com.example.user_management.repository;

import com.example.user_management.entity.User;
import com.example.user_management.enums.VerificationStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    // You can add custom query methods here if needed
    User findByEmail(String email);
    // Or query by other attributes
    User findByIdAndEmail(String id, String email);

    @Query("{'kyc.verified': ?0}")
    List<User> findByKycVerified(VerificationStatus status);

    // Find users by location fields
    //List<User> findByCity(String city);
    //List<User> findByCountry(String country);
    //List<User> findByPostalCode(String postalCode);

    // Find users by combined location criteria
    //List<User> findByCityAndCountry(String city, String country);
    //List<User> findByCityAndCountryAndPostalCode(String city, String country, String postalCode);

    Optional<User> findById(UUID userId);
}

