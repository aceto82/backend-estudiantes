package com.backend.estudiantes.services;

import com.backend.estudiantes.service.JwtService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET =
            Base64.getEncoder().encodeToString("mi-super-secreto-para-tests-jwt-hmac256".getBytes());
    private static final long EXPIRATION_MS = 3_600_000L; // 1 hour

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", EXPIRATION_MS);
    }

    private UserDetails buildUser(String email) {
        return User.withUsername(email).password("pass").authorities(List.of()).build();
    }

    @Test
    void cuandoSeGeneraToken_sujetoEsElEmail() {
        UserDetails userDetails = buildUser("test@example.com");

        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals("test@example.com", jwtService.extractUsername(token));
    }

    @Test
    void cuandoSeGeneraToken_isTokenValidRetornaTrue() {
        UserDetails userDetails = buildUser("valid@example.com");

        String token = jwtService.generateToken(userDetails);

        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void cuandoSeGeneraTokenConClaimsExtra_claimEstaPresente() {
        UserDetails userDetails = buildUser("extra@example.com");

        String token = jwtService.generateToken(Map.of("rol", "ADMIN"), userDetails);
        Claims claims = jwtService.extractAllClaims(token);

        assertEquals("ADMIN", claims.get("rol", String.class));
    }

    @Test
    void cuandoSeAnalizaTokenFirmado_noLanzaExcepcion() {
        UserDetails userDetails = buildUser("parse@example.com");
        String token = jwtService.generateToken(userDetails);

        assertDoesNotThrow(() -> {
            Claims claims = jwtService.extractAllClaims(token);
            assertEquals("parse@example.com", claims.getSubject());
        });
    }
}
