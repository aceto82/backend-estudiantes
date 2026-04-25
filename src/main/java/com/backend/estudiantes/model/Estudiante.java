package com.backend.estudiantes.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "estudiantes")
@Data
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "codigo_estudiantil", nullable = false, unique = true, length = 10)
    private String codigoEstudiantil;

    @Column(name = "horas_acumuladas", nullable = false)
    private Integer horasAcumuladas;

    @Column(nullable = false, length = 100)
    private String programaAcademico;

    @Column(nullable = false)
    private Integer semestre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEstudiante estado = EstadoEstudiante.ACTIVO;

    public String getNombreCompleto() {
        return usuario.getNombre();
    }

}
