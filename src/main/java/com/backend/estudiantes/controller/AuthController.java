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
            String token = authServices.authenticate(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(AuthResponseBuilder.buildLoginSuccess(request.getEmail(), token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(AuthResponseBuilder.buildError(e.getMessage()));
        }
    }
}
