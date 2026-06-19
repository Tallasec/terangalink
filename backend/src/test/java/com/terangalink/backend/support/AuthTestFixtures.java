package com.terangalink.backend.support;

import com.terangalink.backend.config.JwtProperties;
import com.terangalink.backend.entity.User;
import com.terangalink.backend.enums.Role;
import com.terangalink.backend.requestDTO.LoginRequestDTO;
import com.terangalink.backend.security.JwtService;
import com.terangalink.backend.security.UserPrincipal;

import java.time.LocalDateTime;

public final class AuthTestFixtures {

    public static final String JWT_TEST_SECRET =
            "terangalink-test-jwt-secret-key-256-bits-minimum-length-required";

    private AuthTestFixtures() {
    }

    public static JwtProperties testJwtProperties() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret(JWT_TEST_SECRET);
        properties.setExpirationMs(3_600_000L);
        return properties;
    }

    public static JwtService testJwtService() {
        return new JwtService(testJwtProperties());
    }

    public static LoginRequestDTO validLoginRequest() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail(UserTestFixtures.VALID_EMAIL);
        dto.setPassword(UserTestFixtures.VALID_PASSWORD);
        return dto;
    }

    public static UserPrincipal sampleUserPrincipal(Long id) {
        User user = UserTestFixtures.sampleUser(id);
        return UserPrincipal.from(user);
    }

    public static UserPrincipal adminUserPrincipal(Long id) {
        User user = UserTestFixtures.sampleUser(id);
        user.setRole(Role.ADMIN);
        user.setEmail("admin@example.com");
        user.setCreatedAt(LocalDateTime.of(2025, 1, 15, 10, 30));
        return UserPrincipal.from(user);
    }
}
