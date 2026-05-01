package com.backend.estudiantes.utils;

import com.backend.estudiantes.dto.UsuarioInfo;
import com.backend.estudiantes.model.Role;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AuthResponseBuilderTest {

    private UsuarioInfo buildUsuarioInfo() {
        return UsuarioInfo.builder()
                .id(1L)
                .email("user@example.com")
                .rol(Role.ESTUDIANTE)
                .nombre("Ana")
                .apellido("López")
                .build();
    }

    @Test
    void buildLoginSuccess_contieneLosCincoClaveRaizYNoEmail() {
        Map<String, Object> response = AuthResponseBuilder.buildLoginSuccess(
                buildUsuarioInfo(), "eyJ.access", "uuid-refresh", 3600L);

        assertEquals(5, response.size());
        assertTrue(response.containsKey("usuario"));
        assertTrue(response.containsKey("accessToken"));
        assertTrue(response.containsKey("refreshToken"));
        assertTrue(response.containsKey("tokenType"));
        assertTrue(response.containsKey("expiresIn"));
        assertFalse(response.containsKey("email"));
    }

    @Test
    void buildLoginSuccess_usuarioContieneLosCincoSubcampos() {
        Map<String, Object> response = AuthResponseBuilder.buildLoginSuccess(
                buildUsuarioInfo(), "eyJ.access", "uuid-refresh", 3600L);

        @SuppressWarnings("unchecked")
        Map<String, Object> usuario = (Map<String, Object>) response.get("usuario");

        assertEquals(1L, usuario.get("id"));
        assertEquals("user@example.com", usuario.get("email"));
        assertEquals(Role.ESTUDIANTE, usuario.get("rol"));
        assertEquals("Ana", usuario.get("nombre"));
        assertEquals("López", usuario.get("apellido"));
    }

    @Test
    void buildLoginSuccess_tokenTypeEsBearer() {
        Map<String, Object> response = AuthResponseBuilder.buildLoginSuccess(
                buildUsuarioInfo(), "eyJ.access", "uuid-refresh", 3600L);

        assertEquals("Bearer", response.get("tokenType"));
    }

    @Test
    void buildLoginSuccess_expiresInRefleja3600() {
        Map<String, Object> response = AuthResponseBuilder.buildLoginSuccess(
                buildUsuarioInfo(), "eyJ.access", "uuid-refresh", 3600L);

        assertEquals(3600L, response.get("expiresIn"));
    }

    @Test
    void buildRefreshSuccess_contieneCuatroClavesSinUsuario() {
        Map<String, Object> response = AuthResponseBuilder.buildRefreshSuccess(
                "eyJ.access", "uuid-refresh", 3600L);

        assertEquals(4, response.size());
        assertEquals("eyJ.access", response.get("accessToken"));
        assertEquals("uuid-refresh", response.get("refreshToken"));
        assertEquals("Bearer", response.get("tokenType"));
        assertEquals(3600L, response.get("expiresIn"));
        assertFalse(response.containsKey("usuario"));
    }

    @Test
    void buildError_contieneError() {
        Map<String, Object> response = AuthResponseBuilder.buildError("Contraseña incorrecta");

        assertEquals("Contraseña incorrecta", response.get("error"));
        assertEquals(1, response.size());
    }
}
