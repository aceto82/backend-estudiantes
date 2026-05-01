package com.backend.estudiantes.utils;

import com.backend.estudiantes.dto.UsuarioInfo;

import java.util.Map;

public class AuthResponseBuilder {

    private static final String TOKEN_TYPE = "Bearer";

    private AuthResponseBuilder() {}

    public static Map<String, Object> buildLoginSuccess(
            UsuarioInfo usuario, String accessToken, String refreshToken, long expiresIn) {
        Map<String, Object> usuarioMap = Map.of(
                "id", usuario.getId(),
                "email", usuario.getEmail(),
                "rol", usuario.getRol(),
                "nombre", usuario.getNombre(),
                "apellido", usuario.getApellido()
        );
        return Map.of(
                "usuario", usuarioMap,
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "tokenType", TOKEN_TYPE,
                "expiresIn", expiresIn
        );
    }

    public static Map<String, Object> buildError(String message) {
        return Map.of("error", message);
    }
}
