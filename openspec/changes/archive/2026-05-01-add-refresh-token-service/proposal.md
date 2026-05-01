## Why

`RefreshTokenRepository` está implementado y testeado, pero no existe lógica de negocio que lo utilice. Sin un servicio dedicado, el flujo de login no puede emitir refresh tokens, y no hay mecanismo para rotarlos, revocarlos ni limpiar los expirados.

## What Changes

- Nuevo `RefreshTokenService` con cinco operaciones: crear token, buscar y validar por string, borrar todos los tokens de un usuario, rotar un token (soft-delete del viejo + creación del nuevo), y limpiar tokens expirados
- Nuevo `dto/AuthTokensResponse` como tipo de retorno unificado que encapsula `accessToken` y `refreshToken`
- `AuthServices.authenticate()` pasa de devolver `String` a devolver `AuthTokensResponse`, creando también el refresh token en la misma transacción de login
- `AuthController.login()` y `AuthResponseBuilder.buildLoginSuccess()` actualizados para exponer ambos tokens en la respuesta HTTP
- Nueva propiedad de configuración `app.refresh-token.expirationMs` para el TTL del refresh token

## Capabilities

### New Capabilities

- `refresh-token-service`: Lógica de negocio para crear, validar, rotar y limpiar refresh tokens persistidos en base de datos

### Modified Capabilities

- `auth-response-builder`: La firma de `buildLoginSuccess` cambia para incluir `refreshToken` además del `accessToken`

## Impact

- `service/RefreshTokenService.java` — archivo nuevo
- `dto/AuthTokensResponse.java` — archivo nuevo
- `service/AuthServices.java` — cambia tipo de retorno de `authenticate()`
- `controller/AuthController.java` — consume el nuevo `AuthTokensResponse`
- `utils/AuthResponseBuilder.java` — firma actualizada de `buildLoginSuccess`
- `src/main/resources/application.properties` — nueva propiedad `app.refresh-token.expirationMs`
- `test/services/RefreshTokenServiceTest.java` — tests unitarios nuevos
- `test/utils/AuthResponseBuilderTest.java` — test actualizado para la nueva firma
- Sin cambios en dependencias Maven