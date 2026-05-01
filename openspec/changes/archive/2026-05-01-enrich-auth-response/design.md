## Context

La respuesta de login actual (`AuthResponseBuilder.buildLoginSuccess`) devuelve tres campos: `email`, `accessToken`, `refreshToken`. Esta estructura obliga al cliente a recordar el email por separado y no incluye metadatos estándar como `tokenType` ni `expiresIn`. El cambio enriquece la respuesta sin alterar el flujo de autenticación ni la estructura de la base de datos.

Estado actual:
```json
{ "email": "...", "accessToken": "...", "refreshToken": "..." }
```

Estado objetivo:
```json
{
  "usuario": { "id": 1, "email": "...", "rol": "ESTUDIANTE", "nombre": "...", "apellido": "..." },
  "accessToken": "...",
  "refreshToken": "...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

## Goals / Non-Goals

**Goals:**
- Incluir datos públicos del usuario autenticado en la respuesta de login
- Agregar `tokenType` y `expiresIn` alineados con el estándar OAuth2 (RFC 6749)
- Evitar exponer campos sensibles (`password`, `activo`) en la respuesta

**Non-Goals:**
- Cambiar el flujo de autenticación o la validación de credenciales
- Agregar un endpoint dedicado de perfil de usuario
- Modificar la estructura de la base de datos

## Decisions

### D1 — `UsuarioInfo` como DTO separado, no `Map` inline

**Decisión:** nuevo `dto/UsuarioInfo` con los 5 campos explícitos.

**Alternativa descartada:** construir un `Map<String, Object>` directamente en `AuthResponseBuilder`.

**Razón:** el DTO es tipado, testeado y reutilizable en futuros endpoints (perfil, refresh). Un `Map` inline en el builder acopla la representación de usuario a la clase de respuesta de auth.

---

### D2 — Firma del builder con campos sueltos, no recibe `AuthTokensResponse`

**Decisión:** `buildLoginSuccess(UsuarioInfo, String, String, long)`.

**Alternativa descartada:** `buildLoginSuccess(AuthTokensResponse tokens)`.

**Razón:** mantiene `AuthResponseBuilder` (en `utils/`) independiente de los DTOs de `dto/`. El builder es una utilidad de presentación; no debe conocer la estructura interna del servicio.

---

### D3 — `tokenType` como constante privada en el builder

**Decisión:** `private static final String TOKEN_TYPE = "Bearer"` — nunca se pasa como parámetro.

**Razón:** el sistema solo emite tokens Bearer. Parametrizar algo que nunca cambia agrega ruido sin valor. Si en el futuro se necesitan otros esquemas, se puede refactorizar entonces.

---

### D4 — `expiresIn` calculado en `AuthServices`, en segundos

**Decisión:** `expiresIn = jwtService.getExpirationMs() / 1000` en `AuthServices.authenticate()`.

**Razón:** `AuthServices` ya conoce `JwtService` y es el lugar correcto para orquestar la construcción del DTO de respuesta. El builder recibe el valor ya calculado; no debe acceder a servicios. Los segundos siguen el estándar RFC 6749.

## Risks / Trade-offs

- **Breaking change en la respuesta** → cualquier cliente que lea la clave `"email"` top-level dejará de encontrarla; ahora está en `"usuario.email"`. Mitigación: documentar el cambio antes de desplegar.
- **`UsuarioInfo` duplica campos de `Usuario`** → aceptable; es un DTO de presentación deliberadamente acotado. Si `Usuario` cambia nombre/apellido, `UsuarioInfo` deberá actualizarse también.
