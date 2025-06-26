package com.example.user_management.mapper;

import com.example.user_management.dto.UserCreateDTO;
import com.example.user_management.dto.UserResponseDTO;
import com.example.user_management.entity.User;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserMapper {

    private final KYCVerificationMapper kycVerificationMapper;

    public UserMapper(KYCVerificationMapper kycVerificationMapper) {
        this.kycVerificationMapper = kycVerificationMapper;
    }

    public User toUser(UserCreateDTO dto) {
        if (dto == null) return null;

        User user = new User();
        user.setFirstname(dto.getFirstname());
        user.setLastname(dto.getLastname());
        user.setEmail(dto.getEmail());
        user.setAccountLocked(dto.isAccountLocked());
        user.setEnabled(dto.isEnabled());
        user.setCreatedDate(dto.getCreatedDate());
        user.setDateOfBirth(dto.getDateOfBirth());

        if (dto.getKyc() != null) {
            user.setKyc(kycVerificationMapper.toKYCVerification(dto.getKyc()));
        }

        return user;
    }

    public UserResponseDTO toUserResponseDTO(User user) {
        if (user == null) return null;

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(UUID.fromString(user.getId()));
        dto.setFirstname(user.getFirstname());
        dto.setLastname(user.getLastname());
        dto.setEmail(user.getEmail());
        dto.setAccountLocked(user.isAccountLocked());
        dto.setEnabled(user.isEnabled());
        dto.setCreatedDate(user.getCreatedDate());
        dto.setDateOfBirth(user.getDateOfBirth());

        if (user.getKyc() != null) {
            dto.setKyc(kycVerificationMapper.toKYCVerificationDTO(user.getKyc()));
        }

        return dto;
    }

}
