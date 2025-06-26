package com.example.user_management.service;

import com.example.user_management.dto.UserCreateDTO;
import com.example.user_management.dto.LoginRequestDTO;
import com.example.user_management.dto.LoginResponseDTO;
import com.example.user_management.enums.Role;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.*;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.util.Map;

import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class KeycloakService {

    private final Keycloak keycloak;
    private final RestTemplate restTemplate;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.urls.auth}")
    private String authServerUrl;

    @Value("${keycloak.adminClientId}")
    private String clientId;

    @Value("${keycloak.adminClientSecret}")
    private String clientSecret;

    public LoginResponseDTO authenticateUser(LoginRequestDTO loginRequest) {
        try {
            System.out.println("Tentative login: " + loginRequest.getUsername() + " / " + loginRequest.getPassword());

            // Construction du body comme en PowerShell
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);
            body.add("grant_type", "password");
            body.add("username", loginRequest.getUsername());
            body.add("password", loginRequest.getPassword());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token",
                request,
                Map.class
            );

            Map<String, Object> tokenResponse = response.getBody();
            String accessToken = (String) tokenResponse.get("access_token");

            // Décoder le JWT pour extraire les infos utilisateur avec Jackson
            String[] splitToken = accessToken.split("\\.");
            String base64EncodedBody = splitToken[1];
            String bodyJson = new String(Base64.getUrlDecoder().decode(base64EncodedBody));
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> json = mapper.readValue(bodyJson, Map.class);

            String username = (String) json.get("preferred_username");
            String email = (String) json.get("email");
            String firstname = (String) json.get("given_name");
            String lastname = (String) json.get("family_name");
            String userId = (String) json.get("sub");

            // Récupérer le rôle métier (BUYER, SELLER, NGO, etc.)
            String role = null;
            Map<String, Object> realmAccess = (Map<String, Object>) json.get("realm_access");
            if (realmAccess != null && realmAccess.get("roles") instanceof java.util.List) {
                java.util.List roles = (java.util.List) realmAccess.get("roles");
                System.out.println("Rôles trouvés dans le token : " + roles); // Log debug
                for (Object r : roles) {
                    String rStr = r.toString().toUpperCase();
                    // Ignore les rôles techniques
                    if (!rStr.startsWith("DEFAULT-ROLES") && !rStr.equals("OFFLINE_ACCESS") && !rStr.equals("UMA_AUTHORIZATION")) {
                        role = rStr;
                        break;
                    }
                }
                if (role == null && !roles.isEmpty()) {
                    role = roles.get(0).toString().toUpperCase();
                }
            }

            return LoginResponseDTO.builder()
                    .access_token(accessToken)
                    .refresh_token((String) tokenResponse.get("refresh_token"))
                    .token_type((String) tokenResponse.get("token_type"))
                    .expires_in((Integer) tokenResponse.get("expires_in"))
                    .user_id(userId)
                    .username(username)
                    .email(email)
                    .firstname(firstname)
                    .lastname(lastname)
                    .role(role)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Échec de l'authentification: " + e.getMessage());
        }
    }

    // Méthode utilitaire pour extraire le rôle principal
    private String extractRoleFromUserInfo(Map<String, Object> userInfo) {
        Object realmAccessObj = userInfo.get("realm_access");
        if (realmAccessObj instanceof Map) {
            Map<String, Object> realmAccess = (Map<String, Object>) realmAccessObj;
            Object rolesObj = realmAccess.get("roles");
            if (rolesObj instanceof java.util.List) {
                java.util.List roles = (java.util.List) rolesObj;
                if (!roles.isEmpty()) {
                    // Prend le premier rôle non "default-roles-surplus"
                    for (Object r : roles) {
                        String role = r.toString();
                        if (!role.startsWith("default-roles")) {
                            return role;
                        }
                    }
                    // Sinon retourne le premier
                    return roles.get(0).toString();
                }
            }
        }
        return null;
    }

    public String getUserInfo(String token) {
        try {
            String url = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/userinfo";
            
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Impossible d'obtenir les informations utilisateur: " + e.getMessage());
        }
    }

    private String getUserRole(String userId) {
        try {
            UserResource userResource = keycloak.realm(realm).users().get(userId);
            List<RoleRepresentation> roles = userResource.roles().realmLevel().listEffective();
            
            if (!roles.isEmpty()) {
                return roles.get(0).getName();
            }
            return "USER"; // Rôle par défaut
        } catch (Exception e) {
            return "USER"; // Rôle par défaut en cas d'erreur
        }
    }

    public Response registerUser(UserCreateDTO userDTO) {
        CredentialRepresentation credentials = new CredentialRepresentation();
        credentials.setTemporary(false);
        credentials.setType(CredentialRepresentation.PASSWORD);
        credentials.setValue(userDTO.getPassword());

        UserRepresentation user = new UserRepresentation();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstname());
        user.setLastName(userDTO.getLastname());
        user.setCredentials(Collections.singletonList(credentials));
        user.setEnabled(true);
        user.setEmailVerified(true);


        // Create the user
        UsersResource usersResource = keycloak.realm(realm).users();
        Response response = usersResource.create(user);

        if (response.getStatus() == 201 && userDTO.getRole() != null) {
            String userId = extractUserIdFromLocationHeader(response);
            assignRealmRole(userId, userDTO.getRole());
        }

        return response;
    }

    private void assignRealmRole(String userId, Object roleName) {
        String roleStr = roleName.toString().trim(); // Enlève juste les espaces
        System.out.println(">>> Tentative d'attribution du rôle : " + roleStr);

        RealmResource realmResource = keycloak.realm(realm);

        // Check if role exists
        RoleRepresentation role;
        try {
            role = realmResource.roles().get(roleStr).toRepresentation();
        } catch (Exception e) {
            throw new RuntimeException("Role '" + roleStr + "' does not exist in Keycloak.");
        }

        UserResource userResource = realmResource.users().get(userId);
        userResource.roles().realmLevel().add(Collections.singletonList(role));
    }

    public String extractUserIdFromLocationHeader(Response response) {
        String location = response.getHeaderString("Location");
        if (location == null) {
            throw new RuntimeException("Missing 'Location' header from Keycloak response.");
        }
        return location.replaceAll(".*/([^/]+)$", "$1");
    }
}
