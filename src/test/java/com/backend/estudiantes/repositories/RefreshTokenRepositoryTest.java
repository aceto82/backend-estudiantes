package com.backend.estudiantes.repositories;

import com.backend.estudiantes.model.RefreshToken;
import com.backend.estudiantes.model.Role;
import com.backend.estudiantes.model.Usuario;
import com.backend.estudiantes.repository.RefreshTokenRepository;
import com.backend.estudiantes.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = usuarioRepository.save(new Usuario(
                "Ana", "García", "ana@test.com", "hashed", Role.ESTUDIANTE
        ));
    }

    private RefreshToken buildToken(Usuario owner) {
        return RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .usuario(owner)
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }

    @Test
    void findByToken_tokenExistente_retornaRefreshToken() {
        RefreshToken saved = refreshTokenRepository.save(buildToken(usuario));

        Optional<RefreshToken> found = refreshTokenRepository.findByToken(saved.getToken());

        assertTrue(found.isPresent());
        assertEquals(saved.getToken(), found.get().getToken());
        assertEquals(usuario.getId(), found.get().getUsuario().getId());
    }

    @Test
    void findByToken_tokenInexistente_retornaVacio() {
        Optional<RefreshToken> found = refreshTokenRepository.findByToken("token-inexistente");

        assertFalse(found.isPresent());
    }

    @Test
    void deleteByUsuario_eliminaTodosLosTokensDelUsuario_yDejaIntactosLosDeOtros() {
        Usuario otro = usuarioRepository.save(new Usuario(
                "Luis", "Pérez", "luis@test.com", "hashed", Role.INSTRUCTOR
        ));

        refreshTokenRepository.save(buildToken(usuario));
        refreshTokenRepository.save(buildToken(usuario));
        RefreshToken tokenOtro = refreshTokenRepository.save(buildToken(otro));

        refreshTokenRepository.deleteByUsuario(usuario);

        assertEquals(0, refreshTokenRepository.findAll().stream()
                .filter(t -> t.getUsuario().getId().equals(usuario.getId()))
                .count());
        assertTrue(refreshTokenRepository.findByToken(tokenOtro.getToken()).isPresent());
    }
}
