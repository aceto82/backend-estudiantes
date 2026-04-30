package com.backend.estudiantes.service;

import com.backend.estudiantes.model.Role;
import com.backend.estudiantes.model.Usuario;
import com.backend.estudiantes.repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class Data {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init(){
//        Usuario admin = new Usuario();
//        admin.setEmail("zitro13@gmail.com");
//        admin.setPassword(passwordEncoder.encode("Admin123"));
//        admin.setRol(Role.ADMIN);
//        admin.setNombre("Diego");
//        admin.setApellido("Ortiz");
//        usuarioRepository.save(admin);
    }
}
