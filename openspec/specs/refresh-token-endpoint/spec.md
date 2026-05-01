## Requirements

### Requirement: Endpoint de renovación de tokens
El sistema SHALL exponer `POST /api/auth/refresh-token` que acepte un `RefreshTokenRequest` con campo `refreshToken` no en blanco. El endpoint SHALL validar el refresh token mediante `RefreshTokenService.buscarPorToken`, rotarlo mediante `RefreshTokenService.rotar`, generar un nuevo access token JWT con `JwtService.generateToken` usando el `Usuario` del token rotado, y devolver HTTP 200 con la estructura `{ accessToken, refreshToken, tokenType, expiresIn }`. Si el refresh token es inválido, expirado o revocado, el endpoint SHALL devolver HTTP 400 con `{ error: "<mensaje>" }`.

#### Scenario: Renovación exitosa con token válido
- **WHEN** `POST /api/auth/refresh-token` es llamado con un `refreshToken` existente, no expirado y no revocado
- **THEN** se devuelve HTTP 200
- **THEN** la respuesta contiene `accessToken` con un JWT válido
- **THEN** la respuesta contiene un `refreshToken` nuevo distinto al enviado
- **THEN** la respuesta contiene `tokenType` igual a `"Bearer"`
- **THEN** la respuesta contiene `expiresIn` con el TTL del access token en segundos

#### Scenario: Renovación con token expirado devuelve 400
- **WHEN** `POST /api/auth/refresh-token` es llamado con un `refreshToken` cuyo `expiresAt` es anterior al instante actual
- **THEN** se devuelve HTTP 400
- **THEN** la respuesta contiene la clave `"error"` con un mensaje descriptivo

#### Scenario: Renovación con token revocado devuelve 400
- **WHEN** `POST /api/auth/refresh-token` es llamado con un `refreshToken` cuyo campo `revoked` es `true`
- **THEN** se devuelve HTTP 400
- **THEN** la respuesta contiene la clave `"error"`

#### Scenario: Renovación con token inexistente devuelve 400
- **WHEN** `POST /api/auth/refresh-token` es llamado con un `refreshToken` que no existe en base de datos
- **THEN** se devuelve HTTP 400
- **THEN** la respuesta contiene la clave `"error"`

#### Scenario: Validación de campo en blanco devuelve 400
- **WHEN** `POST /api/auth/refresh-token` es llamado con `refreshToken` vacío o nulo
- **THEN** se devuelve HTTP 400
