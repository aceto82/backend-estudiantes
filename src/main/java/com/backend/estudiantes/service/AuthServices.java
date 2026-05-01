package com.backend.estudiantes.service;

import com.backend.estudiantes.dto.AuthTokensResponse;
import com.backend.estudiantes.dto.UsuarioInfo;
import com.backend.estudiantes.model.RefreshToken;
import com.backend.estudiantes.model.Usuario;
import com.backend.estudiantes.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthServices {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    public AuthTokensResponse authenticate(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String accessToken = jwtService.generateToken(Map.of("rol", usuario.getRol()), usuario);
        RefreshToken refreshToken = refreshTokenService.crear(usuario);

        UsuarioInfo usuarioInfo = UsuarioInfo.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .build();

        return AuthTokensResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .usuario(usuarioInfo)
                .expiresIn(jwtService.getExpirationMs() / 1000)
                .build();
    }
}
