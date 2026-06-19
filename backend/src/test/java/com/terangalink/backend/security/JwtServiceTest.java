package com.terangalink.backend.security;

import com.terangalink.backend.support.AuthTestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = AuthTestFixtures.testJwtService();
    }

    @Test
    void generateToken_shouldProduceValidTokenWithExpectedClaims() {
        UserPrincipal principal = AuthTestFixtures.sampleUserPrincipal(42L);

        String token = jwtService.generateToken(principal);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo(principal.getUsername());
        assertThat(jwtService.validateToken(token)).isTrue();
        assertThat(jwtService.extractExpiration(token)).isAfter(new Date());
    }

    @Test
    void validateToken_shouldRejectCorruptedToken() {
        assertThat(jwtService.validateToken("invalid.token.value")).isFalse();
    }

    @Test
    void validateToken_shouldRejectTokenSignedWithDifferentSecret() {
        UserPrincipal principal = AuthTestFixtures.sampleUserPrincipal(1L);
        String token = jwtService.generateToken(principal);

        JwtService otherJwtService = new JwtService(otherJwtPropertiesWithDifferentSecret());

        assertThat(otherJwtService.validateToken(token)).isFalse();
    }

    @Test
    void validateToken_shouldRejectExpiredToken() {
        var properties = AuthTestFixtures.testJwtProperties();
        properties.setExpirationMs(-1_000L);
        JwtService expiredJwtService = new JwtService(properties);

        String token = expiredJwtService.generateToken(AuthTestFixtures.sampleUserPrincipal(1L));

        assertThat(jwtService.validateToken(token)).isFalse();
    }

    private com.terangalink.backend.config.JwtProperties otherJwtPropertiesWithDifferentSecret() {
        var properties = AuthTestFixtures.testJwtProperties();
        properties.setSecret("another-test-jwt-secret-key-256-bits-minimum-length-required");
        return properties;
    }
}
