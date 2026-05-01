package com.backend.estudiantes.dto;

import com.backend.estudiantes.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioInfo {
    private Long id;
    private String email;
    private Role rol;
    private String nombre;
    private String apellido;
}
