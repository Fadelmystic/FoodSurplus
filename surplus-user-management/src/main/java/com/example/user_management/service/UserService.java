package com.example.user_management.service;

import com.example.user_management.dto.UserCreateDTO;
import com.example.user_management.dto.UserUpdateDTO;
import com.example.user_management.entity.User;
import com.example.user_management.enums.Role;
import com.example.user_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void saveUserToMongo(UserCreateDTO dto, UUID keycloakUserId) {
        User user = User.builder()
                .id(keycloakUserId.toString())  // <-- pass String, not UUID
                .firstname(dto.getFirstname())
                .lastname(dto.getLastname())
                .email(dto.getEmail())
                .username(dto.getUsername())
                .enabled(true)
                .accountLocked(dto.isAccountLocked())
                .createdDate(dto.getCreatedDate() != null ? dto.getCreatedDate() : LocalDateTime.now())
                .role(dto.getRole() != null ? dto.getRole() : Role.USER)
                .build();

        userRepository.save(user);
    }



    public void updateUserInMongo(String userId, UserUpdateDTO dto) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User with ID " + userId + " not found.");
        }

        User user = optionalUser.get();

        // Update fields only if present
        if (dto.getFirstname() != null) user.setFirstname(dto.getFirstname());
        if (dto.getLastname() != null) user.setLastname(dto.getLastname());
        if (dto.getUsername() != null) user.setUsername(dto.getUsername());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getRole() != null) user.setRole(dto.getRole());
        if (dto.getEnabled() != null) user.setEnabled(dto.getEnabled());
        if (dto.getAccountLocked() != null) user.setAccountLocked(dto.getAccountLocked());

        userRepository.save(user);
    }

    public boolean isUserEnabled(String userId) {
        return userRepository.findById(userId)
                .map(User::isEnabled)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }



}
