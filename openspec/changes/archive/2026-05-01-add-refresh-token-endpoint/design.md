## Context

El flujo de autenticación emite refresh tokens al hacer login, pero no existe un endpoint para utilizarlos. El cliente queda sin mecanismo para renovar la sesión sin re-autenticarse. Este cambio cierra ese ciclo agregando `POST /api/auth/refresh-token`.

Estado actual del controlador:
```
AuthController
  POST /api/auth/login  →  AuthServices.authenticate()
```

Estado objetivo:
```
AuthController
  POST /api/auth/login         →  AuthServices.authenticate()
  POST /api/auth/refresh-token →  RefreshTokenService + JwtService (directo)
```

## Goals / Non-Goals

**Goals:**
- Exponer el endpoint de renovación de tokens
- Refactorizar `rotarToken(String)` para eliminar duplicación interna
- Mantener la respuesta del endpoint consistente con el estándar OAuth2 (sin datos de usuario, solo tokens)

**Non-Goals:**
- Agregar autenticación al endpoint `/refresh-token` (ya está cubierto por el refresh token mismo)
- Implementar revocación manual / logout (cambio futuro)
- Agregar `@Scheduled` para limpieza de tokens expirados

## Decisions

### D1 — El controller orquesta directamente, sin pasar por `AuthServices`

**Decisión:** `AuthController` inyecta `RefreshTokenService` y `JwtService` directamente.

**Alternativa descartada:** agregar `AuthServices.refresh(String token) → AuthTokensResponse`.

**Razón:** el flujo de refresh es más simple que el login (no hay validación de credenciales, no hay construcción de `UsuarioInfo`). Agregar un método a `AuthServices` solo por consistencia añade indirección sin beneficio real. El controller maneja el caso con 4 llamadas simples y lineales.

---

### D2 — `rotar(RefreshToken)` como método primario, `rotarToken(String)` como wrapper

**Decisión:** se agrega `rotar(RefreshToken tokenViejo)` que opera sobre la entidad. `rotarToken(String)` se refactoriza para delegar en `rotar(buscarPorToken(tokenViejo))`.

**Alternativa descartada:** eliminar `rotarToken(String)` completamente.

**Razón:** `rotarToken(String)` tiene tests existentes y puede tener callers futuros. Mantenerlo como wrapper de una línea no agrega costo y evita romper código.

**Beneficio:** el controller llama primero a `buscarPorToken` (validación explícita) y luego a `rotar(entity)` sin re-consultar la BD. Una sola consulta de lectura en todo el flujo.

---

### D3 — Respuesta sin datos de usuario (`buildRefreshSuccess`)

**Decisión:** nuevo `buildRefreshSuccess(String accessToken, String refreshToken, long expiresIn)` que devuelve 4 claves: `accessToken`, `refreshToken`, `tokenType`, `expiresIn`.

**Alternativa descartada:** reutilizar `buildLoginSuccess` con un `UsuarioInfo` dummy.

**Razón:** el cliente que hace refresh ya conoce los datos del usuario. Repetirlos en la respuesta es ruido. Mantener métodos separados hace explícito que login y refresh tienen contratos distintos.

## Risks / Trade-offs

- **`rotarToken(String)` como wrapper** → si algún caller pasa un token inválido, la validación ocurre dentro de `buscarPorToken` y la excepción se propaga igual que antes. Sin regresión.
- **Inyección directa en controller** → si en el futuro se necesita lógica transversal (logging, auditoría, métricas) en el flujo de refresh, habrá que extraerla. Por ahora el costo es bajo.
- **Token de la entidad `Usuario` cargado por Hibernate** → `nuevoToken.getUsuario()` requiere que la sesión de Hibernate esté activa. Con `spring.jpa.open-in-view=true` (default en Spring Boot) esto funciona. Sin él, sería necesario un `@Transactional`.
