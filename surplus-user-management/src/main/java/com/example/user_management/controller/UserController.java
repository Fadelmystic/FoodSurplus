package com.example.user_management.controller;

import com.example.user_management.dto.UserCreateDTO;
import com.example.user_management.dto.UserUpdateDTO;
import com.example.user_management.dto.LoginRequestDTO;
import com.example.user_management.dto.LoginResponseDTO;
import com.example.user_management.entity.User;
import com.example.user_management.service.KeycloakService;
import com.example.user_management.service.UserService;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final KeycloakService keycloakService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            LoginResponseDTO response = keycloakService.authenticateUser(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Identifiants invalides: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserCreateDTO userCreateDTO) {
        System.out.println(">>> registerUser appelé ! (depuis proxy)");
        try {
            Response kcResponse = keycloakService.registerUser(userCreateDTO);

            if (kcResponse.getStatus() != 201) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Keycloak registration failed: " + kcResponse.getStatusInfo());
            }

            UUID userId = UUID.fromString(keycloakService.extractUserIdFromLocationHeader(kcResponse));
            userService.saveUserToMongo(userCreateDTO, userId);

            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Registration failed: " + e.getMessage());
        }
    }
    
    @PutMapping("/{userId}")
    public ResponseEntity<Void> updateUser(@PathVariable String userId, @RequestBody UserUpdateDTO dto) {
        userService.updateUserInMongo(userId, dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/enabled")
    public ResponseEntity<Boolean> isUserEnabled(@PathVariable String userId) {
        boolean enabled = userService.isUserEnabled(userId);
        return ResponseEntity.ok(enabled);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            String userInfo = keycloakService.getUserInfo(token.replace("Bearer ", ""));
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token invalide: " + e.getMessage());
        }
    }
}
