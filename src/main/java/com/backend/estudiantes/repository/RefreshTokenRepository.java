package com.backend.estudiantes.repository;

import com.backend.estudiantes.model.RefreshToken;
import com.backend.estudiantes.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByUsuario(Usuario usuario);

    @Transactional
    void deleteByUsuario(Usuario usuario);

    @Transactional
    void deleteByExpiresAtBefore(Instant now);
}
