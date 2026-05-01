package com.backend.estudiantes.utils;

import java.util.Map;

public class AuthResponseBuilder {

    private AuthResponseBuilder() {}

    public static Map<String, Object> buildLoginSuccess(String email, String accessToken, String refreshToken) {
        return Map.of("email", email, "accessToken", accessToken, "refreshToken", refreshToken);
    }

    public static Map<String, Object> buildError(String message) {
        return Map.of("error", message);
    }
}
