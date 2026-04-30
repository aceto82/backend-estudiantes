package com.backend.estudiantes.utils;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AuthResponseBuilderTest {

    @Test
    void buildLoginSuccess_contieneEmailYToken() {
        Map<String, Object> response = AuthResponseBuilder.buildLoginSuccess("user@example.com", "eyJ.token");

        assertEquals("user@example.com", response.get("email"));
        assertEquals("eyJ.token", response.get("token"));
        assertEquals(2, response.size());
    }

    @Test
    void buildError_contieneError() {
        Map<String, Object> response = AuthResponseBuilder.buildError("Contraseña incorrecta");

        assertEquals("Contraseña incorrecta", response.get("error"));
        assertEquals(1, response.size());
    }
}
