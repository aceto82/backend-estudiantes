## Why

El sistema puede emitir refresh tokens al hacer login, pero no existe un endpoint que los consuma. Sin `POST /api/auth/refresh-token`, el cliente no puede renovar su sesión cuando el access token expira y debe re-autenticarse con credenciales, lo que contradice el propósito del refresh token.

## What Changes

- Nuevo `dto/RefreshTokenRequest` con validación `@NotBlank` sobre el campo `refreshToken`
- Nuevo endpoint `POST /api/auth/refresh-token` en `AuthController` — valida el refresh token, lo rota y devuelve un nuevo par de tokens
- Nuevo método `rotar(RefreshToken)` en `RefreshTokenService` — recibe la entidad directamente para evitar doble consulta a BD
- Refactorización interna de `rotarToken(String)` para delegar en `rotar(RefreshToken)` sin duplicar lógica
- Nuevo método `buildRefreshSuccess` en `AuthResponseBuilder` — construye la respuesta de renovación sin datos de usuario

## Capabilities

### New Capabilities

- `refresh-token-endpoint`: Endpoint `POST /api/auth/refresh-token` que valida y rota un refresh token, devolviendo nuevos `accessToken` y `refreshToken`

### Modified Capabilities

- `refresh-token-service`: Se agrega `rotar(RefreshToken)` y se refactoriza `rotarToken(String)` como wrapper — cambio de contrato interno del servicio
- `auth-response-builder`: Se agrega `buildRefreshSuccess` con una firma y estructura de mapa diferente a `buildLoginSuccess`

## Impact

- `dto/RefreshTokenRequest.java` — archivo nuevo
- `service/RefreshTokenService.java` — nuevo método `rotar(RefreshToken)`, refactorización de `rotarToken(String)`
- `utils/AuthResponseBuilder.java` — nuevo método `buildRefreshSuccess`
- `controller/AuthController.java` — nuevas inyecciones y nuevo endpoint
- `test/services/RefreshTokenServiceTest.java` — tests nuevos para `rotar(RefreshToken)`
- `test/utils/AuthResponseBuilderTest.java` — test nuevo para `buildRefreshSuccess`
- Sin cambios en dependencias Maven ni en la base de datos
