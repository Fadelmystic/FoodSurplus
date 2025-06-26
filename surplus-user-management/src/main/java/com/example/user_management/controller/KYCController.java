package com.example.user_management.controller;

import com.example.user_management.entity.KYCVerification;
import com.example.user_management.enums.VerificationStatus;
import com.example.user_management.service.KYCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/kyc")
public class KYCController {

    //private final KYCService kycService;
/*
    @Autowired
    public KYCController(KYCService kycService) {
        this.kycService = kycService;
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<?> findKycByUser(@PathVariable String userId) {
        Optional<KYCVerification> kyc = kycService.findKycByUserId(userId);
        return kyc.map(verification -> new ResponseEntity<>(verification, HttpStatus.OK))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("No KYC found for user: " + userId));
    }


    @GetMapping("/status/{status}")
    public ResponseEntity<List<KYCVerification>> findKycByStatus(@PathVariable VerificationStatus status) {
        List<KYCVerification> kycs = kycService.findKycByStatus(status);
        return new ResponseEntity<>(kycs, HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<KYCVerification> submitKyc(@RequestBody KYCVerification kycVerification) {
        KYCVerification savedKyc = kycService.submitKyc(kycVerification);
        return new ResponseEntity<>(savedKyc, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateKyc(@PathVariable String id, @RequestBody KYCVerification kycVerification) {
        Optional<KYCVerification> updatedKyc = kycService.updateKyc(id, kycVerification);
        return updatedKyc.map(verification -> new ResponseEntity<>(verification, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>("KYC with ID: " + id + " not found", HttpStatus.NOT_FOUND));
    }


    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateKycStatus(@PathVariable String id, @RequestBody VerificationStatus status) {
        Optional<KYCVerification> updatedKyc = kycService.updateKycStatus(id, status);
        return updatedKyc.map(verification -> new ResponseEntity<>(verification, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>("KYC with ID: " + id + " not found", HttpStatus.NOT_FOUND));
    }*/
}