package com.backend.estudiantes.controller;

import com.backend.estudiantes.dto.LoginRequest;
import com.backend.estudiantes.service.AuthServices;
import com.backend.estudiantes.utils.AuthResponseBuilder;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthServices authServices;

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
}
