package com.backend.estudiantes.controller;

import com.backend.estudiantes.dto.LoginRequest;
import com.backend.estudiantes.dto.RefreshTokenRequest;
import com.backend.estudiantes.model.RefreshToken;
import com.backend.estudiantes.model.Usuario;
import com.backend.estudiantes.service.AuthServices;
import com.backend.estudiantes.service.JwtService;
import com.backend.estudiantes.service.RefreshTokenService;
import com.backend.estudiantes.utils.AuthResponseBuilder;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthServices authServices;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            var tokens = authServices.authenticate(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(AuthResponseBuilder.buildLoginSuccess(
                    tokens.getUsuario(), tokens.getAccessToken(), tokens.getRefreshToken(), tokens.getExpiresIn()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(AuthResponseBuilder.buildError(e.getMessage()));
        }
    }

    @PostMapping("refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            RefreshToken tokenEntity = refreshTokenService.buscarPorToken(request.getRefreshToken());
            RefreshToken nuevoToken = refreshTokenService.rotar(tokenEntity);
            Usuario usuario = nuevoToken.getUsuario();
            String accessToken = jwtService.generateToken(Map.of("rol", usuario.getRol()), usuario);
            long expiresIn = jwtService.getExpirationMs() / 1000;
            return ResponseEntity.ok(AuthResponseBuilder.buildRefreshSuccess(
                    accessToken, nuevoToken.getToken(), expiresIn));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(AuthResponseBuilder.buildError(e.getMessage()));
        }
    }
}
