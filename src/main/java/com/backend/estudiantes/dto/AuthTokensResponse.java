package com.backend.estudiantes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthTokensResponse {
    private String accessToken;
    private String refreshToken;
    private UsuarioInfo usuario;
    private long expiresIn;
}
