## ADDED Requirements

### Requirement: Rotar refresh token por entidad
El sistema SHALL proporcionar `RefreshTokenService.rotar(RefreshToken tokenViejo)` que reciba la entidad directamente, marque `revoked = true` en la entidad recibida, la persista con `save()`, cree un nuevo `RefreshToken` para el mismo usuario con `crear()`, y devuelva el nuevo `RefreshToken`. No SHALL realizar ninguna consulta adicional a la base de datos para validar el token, ya que el caller es responsable de la validación previa.

#### Scenario: Rotación exitosa sobre entidad válida
- **WHEN** `rotar(tokenViejo)` es llamado con una entidad `RefreshToken` no revocada
- **THEN** la entidad `tokenViejo` tiene `revoked = true` en base de datos
- **THEN** se devuelve un nuevo `RefreshToken` con un UUID distinto al de `tokenViejo`
- **THEN** el nuevo token tiene `revoked = false` y `expiresAt` en el futuro

## MODIFIED Requirements

### Requirement: Rotar refresh token
El sistema SHALL proporcionar `RefreshTokenService.rotarToken(String tokenViejo)` como método conveniente que delega en `rotar(buscarPorToken(tokenViejo))`. El comportamiento observable (validación, revocación del viejo, creación del nuevo) SHALL ser idéntico al anterior. La implementación interna SHALL no duplicar la lógica de revocación.

#### Scenario: Rotación exitosa
- **WHEN** `rotarToken` es llamado con un token válido, no expirado y no revocado
- **THEN** el token viejo tiene `revoked = true` en base de datos
- **THEN** se devuelve un nuevo `RefreshToken` con un UUID distinto y `revoked = false`

#### Scenario: Rotación con token inválido lanza excepción
- **WHEN** `rotarToken` es llamado con un token inexistente, expirado o ya revocado
- **THEN** se lanza una `RuntimeException` y no se crea ningún token nuevo
