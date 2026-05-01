package com.backend.estudiantes.service;

import com.backend.estudiantes.model.RefreshToken;
import com.backend.estudiantes.model.Usuario;
import com.backend.estudiantes.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Value("${app.refresh-token.expirationMs}")
    private long refreshTokenExpiration;

    public RefreshToken crear(Usuario usuario) {
        RefreshToken token = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .usuario(usuario)
                .expiresAt(Instant.now().plusMillis(refreshTokenExpiration))
                .revoked(false)
                .build();
        return refreshTokenRepository.save(token);
    }

    public RefreshToken buscarPorToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token no encontrado"));

        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expirado");
        }

        if (refreshToken.isRevoked()) {
            throw new RuntimeException("Refresh token revocado");
        }

        return refreshToken;
    }

    public void borrarPorUsuario(Usuario usuario) {
        refreshTokenRepository.deleteByUsuario(usuario);
    }

    public RefreshToken rotarToken(String tokenViejo) {
        RefreshToken tokenActual = buscarPorToken(tokenViejo);
        tokenActual.setRevoked(true);
        refreshTokenRepository.save(tokenActual);
        return crear(tokenActual.getUsuario());
    }

    public void limpiarTokensExpirados() {
        refreshTokenRepository.deleteByExpiresAtBefore(Instant.now());
    }
}
