## ADDED Requirements

### Requirement: UsuarioInfo DTO
El sistema SHALL proporcionar un DTO `UsuarioInfo` con exactamente cinco campos públicos: `Long id`, `String email`, `Role rol`, `String nombre` y `String apellido`. No SHALL incluir `password`, `activo` ni ningún otro campo de la entidad `Usuario`.

#### Scenario: UsuarioInfo contiene solo los campos permitidos
- **WHEN** se construye un `UsuarioInfo` desde un `Usuario` con todos sus campos poblados
- **THEN** el objeto resultante expone `id`, `email`, `rol`, `nombre` y `apellido`
- **THEN** el objeto no expone `password` ni `activo`

#### Scenario: Construcción con builder
- **WHEN** se usa el builder de Lombok para crear un `UsuarioInfo` con los cinco campos
- **THEN** todos los campos son accesibles mediante getters sin lanzar excepción
