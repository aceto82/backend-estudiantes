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
import java.util.List;
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
        return buildToken(owner, Instant.now().plusSeconds(3600));
    }

    private RefreshToken buildToken(Usuario owner, Instant expiresAt) {
        return RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .usuario(owner)
                .expiresAt(expiresAt)
                .build();
    }

    // ── findByToken ────────────────────────────────────────────────────────────

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

    // ── deleteByUsuario ────────────────────────────────────────────────────────

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

    // ── findByUsuario ──────────────────────────────────────────────────────────

    @Test
    void findByUsuario_retornaTodosLosTokensDelUsuario() {
        refreshTokenRepository.save(buildToken(usuario));
        refreshTokenRepository.save(buildToken(usuario));

        List<RefreshToken> tokens = refreshTokenRepository.findByUsuario(usuario);

        assertEquals(2, tokens.size());
        assertTrue(tokens.stream().allMatch(t -> t.getUsuario().getId().equals(usuario.getId())));
    }

    @Test
    void findByUsuario_sinTokens_retornaListaVacia() {
        List<RefreshToken> tokens = refreshTokenRepository.findByUsuario(usuario);

        assertTrue(tokens.isEmpty());
    }

    @Test
    void findByUsuario_noRetornaTokensDeOtrosUsuarios() {
        Usuario otro = usuarioRepository.save(new Usuario(
                "Pedro", "López", "pedro@test.com", "hashed", Role.INSTRUCTOR
        ));
        refreshTokenRepository.save(buildToken(otro));

        List<RefreshToken> tokens = refreshTokenRepository.findByUsuario(usuario);

        assertTrue(tokens.isEmpty());
    }

    // ── deleteByExpiresAtBefore ────────────────────────────────────────────────

    @Test
    void deleteByExpiresAtBefore_eliminaSoloTokensExpirados() {
        Instant pasado = Instant.now().minusSeconds(3600);
        Instant futuro = Instant.now().plusSeconds(3600);

        RefreshToken expirado = refreshTokenRepository.save(buildToken(usuario, pasado));
        RefreshToken vigente  = refreshTokenRepository.save(buildToken(usuario, futuro));

        refreshTokenRepository.deleteByExpiresAtBefore(Instant.now());

        assertFalse(refreshTokenRepository.findByToken(expirado.getToken()).isPresent());
        assertTrue(refreshTokenRepository.findByToken(vigente.getToken()).isPresent());
    }

    @Test
    void deleteByExpiresAtBefore_sinExpirados_noLanzaError() {
        refreshTokenRepository.save(buildToken(usuario, Instant.now().plusSeconds(3600)));

        assertDoesNotThrow(() -> refreshTokenRepository.deleteByExpiresAtBefore(Instant.now()));
        assertEquals(1, refreshTokenRepository.count());
    }
}
