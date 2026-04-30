package com.backend.estudiantes.utils;

import java.util.Map;

public class AuthResponseBuilder {

    private AuthResponseBuilder() {}

    public static Map<String, Object> buildLoginSuccess(String email, String token) {
        return Map.of("email", email, "token", token);
    }

    public static Map<String, Object> buildError(String message) {
        return Map.of("error", message);
    }
}
