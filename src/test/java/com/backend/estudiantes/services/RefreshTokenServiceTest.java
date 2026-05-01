package com.backend.estudiantes.services;

import com.backend.estudiantes.model.RefreshToken;
import com.backend.estudiantes.model.Role;
import com.backend.estudiantes.model.Usuario;
import com.backend.estudiantes.repository.RefreshTokenRepository;
import com.backend.estudiantes.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private static final long EXPIRATION_MS = 604_800_000L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenExpiration", EXPIRATION_MS);
    }

    private Usuario buildUsuario() {
        return new Usuario("Test", "User", "test@example.com", "hashed", Role.ESTUDIANTE);
    }

    private RefreshToken buildToken(Usuario usuario, boolean revoked, Instant expiresAt) {
        return RefreshToken.builder()
                .id(1L)
                .token("uuid-token")
                .usuario(usuario)
                .expiresAt(expiresAt)
                .revoked(revoked)
                .build();
    }

    // --- crear ---

    @Test
    void cuandoUsuarioValido_persisteTokenConCamposCorrectos() {
        Usuario usuario = buildUsuario();
        when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RefreshToken resultado = refreshTokenService.crear(usuario);

        assertNotNull(resultado.getToken());
        assertFalse(resultado.getToken().isBlank());
        assertEquals(usuario, resultado.getUsuario());
        assertFalse(resultado.isRevoked());
        assertTrue(resultado.getExpiresAt().isAfter(Instant.now()));
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void cuandoSeLlamaDosVeces_generaTokensUnicos() {
        Usuario usuario = buildUsuario();
        when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RefreshToken token1 = refreshTokenService.crear(usuario);
        RefreshToken token2 = refreshTokenService.crear(usuario);

        assertNotEquals(token1.getToken(), token2.getToken());
    }

    // --- buscarPorToken ---

    @Test
    void cuandoTokenValido_devuelveEntidad() {
        Usuario usuario = buildUsuario();
        RefreshToken token = buildToken(usuario, false, Instant.now().plusSeconds(3600));
        when(refreshTokenRepository.findByToken("uuid-token")).thenReturn(Optional.of(token));

        RefreshToken resultado = refreshTokenService.buscarPorToken("uuid-token");

        assertEquals(token, resultado);
    }

    @Test
    void cuandoTokenNoExiste_lanzaExcepcion() {
        when(refreshTokenRepository.findByToken("no-existe")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> refreshTokenService.buscarPorToken("no-existe"));
    }

    @Test
    void cuandoTokenExpirado_lanzaExcepcion() {
        Usuario usuario = buildUsuario();
        RefreshToken token = buildToken(usuario, false, Instant.now().minusSeconds(1));
        when(refreshTokenRepository.findByToken("expirado")).thenReturn(Optional.of(token));

        assertThrows(RuntimeException.class, () -> refreshTokenService.buscarPorToken("expirado"));
    }

    @Test
    void cuandoTokenRevocado_lanzaExcepcion() {
        Usuario usuario = buildUsuario();
        RefreshToken token = buildToken(usuario, true, Instant.now().plusSeconds(3600));
        when(refreshTokenRepository.findByToken("revocado")).thenReturn(Optional.of(token));

        assertThrows(RuntimeException.class, () -> refreshTokenService.buscarPorToken("revocado"));
    }

    // --- borrarPorUsuario ---

    @Test
    void cuandoUsuarioConTokens_delegaAlRepositorio() {
        Usuario usuario = buildUsuario();

        refreshTokenService.borrarPorUsuario(usuario);

        verify(refreshTokenRepository).deleteByUsuario(usuario);
    }

    // --- rotarToken ---

    @Test
    void cuandoTokenValido_revocaViejoYCreaNuevo() {
        Usuario usuario = buildUsuario();
        RefreshToken tokenViejo = buildToken(usuario, false, Instant.now().plusSeconds(3600));
        when(refreshTokenRepository.findByToken("token-viejo")).thenReturn(Optional.of(tokenViejo));
        when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RefreshToken tokenNuevo = refreshTokenService.rotarToken("token-viejo");

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository, times(2)).save(captor.capture());

        RefreshToken guardadoViejo = captor.getAllValues().get(0);
        assertTrue(guardadoViejo.isRevoked());

        assertNotEquals("token-viejo", tokenNuevo.getToken());
        assertFalse(tokenNuevo.isRevoked());
    }

    @Test
    void cuandoTokenInvalido_lanzaExcepcionYNoCreaNuevo() {
        when(refreshTokenRepository.findByToken("invalido")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> refreshTokenService.rotarToken("invalido"));
        verify(refreshTokenRepository, never()).save(any());
    }

    // --- limpiarTokensExpirados ---

    @Test
    void delegaConInstanteCorrecto() {
        Instant antes = Instant.now();

        refreshTokenService.limpiarTokensExpirados();

        ArgumentCaptor<Instant> captor = ArgumentCaptor.forClass(Instant.class);
        verify(refreshTokenRepository).deleteByExpiresAtBefore(captor.capture());
        assertFalse(captor.getValue().isBefore(antes));
    }
}
