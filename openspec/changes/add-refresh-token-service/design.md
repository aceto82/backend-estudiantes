## Context

La capa de repositorio para refresh tokens está completa (`RefreshTokenRepository` con todos los métodos de consulta y borrado). El login actual emite solo un access token JWT de corta duración. Sin un servicio de refresh tokens no hay manera de renovar sesiones sin re-autenticarse, ni de revocarlas selectivamente.

Estado actual del flujo de login:
```
AuthController → AuthServices.authenticate() → String (accessToken)
```

Estado objetivo:
```
AuthController → AuthServices.authenticate() → AuthTokensResponse
                                                  ├── accessToken (JWT)
                                                  └── refreshToken (UUID en DB)
```

## Goals / Non-Goals

**Goals:**
- Encapsular toda la lógica de ciclo de vida de refresh tokens en `RefreshTokenService`
- Hacer que el login emita ambos tokens en una sola operación
- Preparar la infraestructura para el endpoint `/api/auth/refresh` (cambio posterior)

**Non-Goals:**
- Implementar el endpoint `/api/auth/refresh` (fuera de scope)
- Implementar el endpoint `/api/auth/logout` (fuera de scope)
- Limpieza automática con `@Scheduled`
- Detección de reutilización de tokens robados

## Decisions

### D1 — `buscarPorToken` lanza excepción, no devuelve Optional

`buscarPorToken(String token)` lanza `RuntimeException` para los tres casos de error (no encontrado, expirado, revocado). Alternativa descartada: `Optional<RefreshToken>`. Razón: el patrón ya establecido en `AuthServices` lanza excepciones; el caller casi nunca diferencia entre "no existe" y "está expirado".

### D2 — Rotación usa soft delete (revoked = true)

`rotarToken` marca el token viejo con `revoked = true` y hace `save()`. Alternativa descartada: hard delete del token viejo. Razón: conservar tokens revocados permite detectar reutilización en un futuro; `limpiarTokensExpirados` eliminará estas filas eventualmente.

### D3 — Logout usa hard delete (`deleteByUsuario`)

`borrarPorUsuario` delega en `deleteByUsuario`, que elimina físicamente todos los tokens del usuario. Razón: logout explícito no requiere historial.

### D4 — `AuthTokensResponse` como DTO simple

Nuevo `dto/AuthTokensResponse` con `accessToken` y `refreshToken` como Strings. Solo transporta datos entre `AuthServices` y `AuthController`, sin lógica adicional.

### D5 — TTL configurable vía `app.refresh-token.expirationMs`

Sigue el mismo patrón que `app.jwt.expirationMs` en `JwtService`. Default de 7 días (604800000 ms) en `application.properties`.

## Risks / Trade-offs

- **Tokens revocados acumulados** → `limpiarTokensExpirados()` los elimina una vez vencido su TTL; riesgo bajo con limpieza periódica.
- **`authenticate()` no es transaccional** → si `generateToken` falla tras persistir el refresh token, queda un registro huérfano. Mitigación: `limpiarTokensExpirados` lo limpia; `@Transactional` se puede agregar en un cambio futuro.
- **`RuntimeException` genérica** → casos de error no distinguibles por tipo. Aceptable por ahora; jerarquía de excepciones custom se agrega cuando se implemente `/api/auth/refresh`.