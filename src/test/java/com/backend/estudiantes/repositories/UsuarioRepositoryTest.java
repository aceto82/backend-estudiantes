package com.backend.estudiantes.repositories;

import com.backend.estudiantes.model.Role;
import com.backend.estudiantes.model.Usuario;
import com.backend.estudiantes.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository repository;

    @Test
    void findByEmail_UsuarioExistente_RetornaUsuario(){
        Usuario testUser = new Usuario(
                "Test",
                "User",
                "test@email.com.co",
                "123",
                Role.ESTUDIANTE
        );

        Usuario guardado = repository.save(testUser);

        Optional<Usuario> encontradoOptional = repository.findByEmail(guardado.getEmail());

        assertTrue(encontradoOptional.isPresent(), "El usuario debería existir en la BD");

        Usuario encontrado = encontradoOptional.get();
        assertEquals("test@email.com.co", encontrado.getEmail(), "El email del usuario encontrado, debería coincidir con el mail de prueba");

    }
}
