package com.example.user_management.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    public static final String ADMIN = "admin";
    public static final String GENERAL = "general";
    private final JwtAuthConverter jwtAuthConverter;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/users/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/me").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/users/{userId}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/users/{userId}/enabled").authenticated()
                        .requestMatchers(HttpMethod.POST, "/public/", "/users").permitAll()
                        .requestMatchers(HttpMethod.GET, "/public/").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/public/").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/public/").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/").permitAll()
                        .requestMatchers("/v3/api-docs/", "/configuration/", "/swagger-ui/",
                                "/swagger-resources/", "/swagger-ui.html", "/webjars/", "/api-docs/").permitAll()
                        .requestMatchers("/api/kyc/").hasRole(ADMIN)
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthConverter)
                        )
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

}