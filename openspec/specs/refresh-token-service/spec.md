## Requirements

### Requirement: Crear refresh token
El sistema SHALL proporcionar `RefreshTokenService.crear(Usuario usuario)` que genere un UUID aleatorio, calcule `expiresAt` sumando `app.refresh-token.expirationMs` al instante actual, construya una entidad `RefreshToken` con `revoked = false` y la persista en base de datos, devolviendo la entidad guardada.

#### Scenario: Token creado con campos correctos
- **WHEN** `crear(usuario)` es llamado con un `Usuario` válido
- **THEN** se persiste un `RefreshToken` con un UUID no nulo como token, `usuario` igual al recibido, `expiresAt` en el futuro, `revoked = false` y `createdAt` no nulo

#### Scenario: Cada llamada genera un token único
- **WHEN** `crear(usuario)` es llamado dos veces con el mismo usuario
- **THEN** los dos `RefreshToken` resultantes tienen valores de `token` distintos

### Requirement: Buscar y validar refresh token por string
El sistema SHALL proporcionar `RefreshTokenService.buscarPorToken(String token)` que recupere la entidad `RefreshToken` correspondiente y lance `RuntimeException` si el token no existe, si su `expiresAt` es anterior al instante actual, o si su campo `revoked` es `true`. Si todas las validaciones pasan, devuelve la entidad.

#### Scenario: Token válido encontrado
- **WHEN** `buscarPorToken` es llamado con un token existente, no expirado y no revocado
- **THEN** se devuelve la entidad `RefreshToken` correspondiente

#### Scenario: Token no encontrado lanza excepción
- **WHEN** `buscarPorToken` es llamado con un string que no existe en base de datos
- **THEN** se lanza una `RuntimeException`

#### Scenario: Token expirado lanza excepción
- **WHEN** `buscarPorToken` es llamado con un token cuyo `expiresAt` es anterior a `Instant.now()`
- **THEN** se lanza una `RuntimeException`

#### Scenario: Token revocado lanza excepción
- **WHEN** `buscarPorToken` es llamado con un token cuyo campo `revoked` es `true`
- **THEN** se lanza una `RuntimeException`

### Requirement: Borrar tokens de un usuario
El sistema SHALL proporcionar `RefreshTokenService.borrarPorUsuario(Usuario usuario)` que elimine físicamente (hard delete) todos los registros `RefreshToken` asociados al usuario dado, delegando en `RefreshTokenRepository.deleteByUsuario`.

#### Scenario: Tokens del usuario eliminados
- **WHEN** `borrarPorUsuario(usuario)` es llamado con un usuario que tiene tokens en base de datos
- **THEN** todos los `RefreshToken` de ese usuario son eliminados de la base de datos

#### Scenario: Sin error cuando el usuario no tiene tokens
- **WHEN** `borrarPorUsuario(usuario)` es llamado con un usuario sin tokens
- **THEN** no se lanza ninguna excepción

### Requirement: Rotar refresh token por entidad
El sistema SHALL proporcionar `RefreshTokenService.rotar(RefreshToken tokenViejo)` que reciba la entidad directamente, marque `revoked = true` en la entidad recibida, la persista con `save()`, cree un nuevo `RefreshToken` para el mismo usuario con `crear()`, y devuelva el nuevo `RefreshToken`. No SHALL realizar ninguna consulta adicional a la base de datos para validar el token, ya que el caller es responsable de la validación previa.

#### Scenario: Rotación exitosa sobre entidad válida
- **WHEN** `rotar(tokenViejo)` es llamado con una entidad `RefreshToken` no revocada
- **THEN** la entidad `tokenViejo` tiene `revoked = true` en base de datos
- **THEN** se devuelve un nuevo `RefreshToken` con un UUID distinto al de `tokenViejo`
- **THEN** el nuevo token tiene `revoked = false` y `expiresAt` en el futuro

### Requirement: Rotar refresh token
El sistema SHALL proporcionar `RefreshTokenService.rotarToken(String tokenViejo)` como método conveniente que delega en `rotar(buscarPorToken(tokenViejo))`. El comportamiento observable (validación, revocación del viejo, creación del nuevo) SHALL ser idéntico al anterior. La implementación interna SHALL no duplicar la lógica de revocación.

#### Scenario: Rotación exitosa
- **WHEN** `rotarToken` es llamado con un token válido, no expirado y no revocado
- **THEN** el token viejo tiene `revoked = true` en base de datos
- **THEN** se devuelve un nuevo `RefreshToken` con un UUID distinto y `revoked = false`

#### Scenario: Rotación con token inválido lanza excepción
- **WHEN** `rotarToken` es llamado con un token inexistente, expirado o ya revocado
- **THEN** se lanza una `RuntimeException` y no se crea ningún token nuevo

### Requirement: Limpiar tokens expirados
El sistema SHALL proporcionar `RefreshTokenService.limpiarTokensExpirados()` que elimine físicamente todos los `RefreshToken` cuyo `expiresAt` sea anterior al instante actual, delegando en `RefreshTokenRepository.deleteByExpiresAtBefore(Instant.now())`.

#### Scenario: Tokens expirados eliminados, vigentes intactos
- **WHEN** `limpiarTokensExpirados()` es llamado con tokens expirados y tokens vigentes en base de datos
- **THEN** solo los tokens expirados son eliminados

#### Scenario: Sin error cuando no hay tokens expirados
- **WHEN** `limpiarTokensExpirados()` es llamado y no existen tokens expirados
- **THEN** no se lanza ninguna excepción
