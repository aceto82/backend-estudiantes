## Context

El filtro JWT está implementado pero solo existe `AuthController` con rutas públicas (`/api/auth/**`). No hay ninguna ruta protegida que se pueda invocar para verificar el flujo completo: login → token → solicitud autenticada. Este controller llena ese vacío con endpoints mínimos.

## Goals / Non-Goals

**Goals:**
- Proveer `GET /api/test/ping` protegido que retorne email y roles del usuario autenticado.
- Proveer `GET /api/test/public` sin autenticación para verificar que el servidor responde.
- Actualizar `SecurityConfig` para permitir `/api/test/public` sin token.

**Non-Goals:**
- Persistencia de datos ni lógica de negocio.
- Paginación, filtros, ni manejo de errores complejos.
- Endpoints de actuator o métricas (ya cubiertos por Spring Actuator).

## Decisions

### D1: Leer usuario autenticado desde `SecurityContextHolder`

`SecurityContextHolder.getContext().getAuthentication().getPrincipal()` retorna el `UserDetails` establecido por el filtro JWT. Es el mecanismo estándar de Spring Security y no requiere inyección adicional. Alternativa descartada: inyectar `Authentication` como parámetro del método del controlador — igualmente válido, pero el approach con `SecurityContextHolder` es más explícito para propósitos de diagnóstico.

### D2: Respuesta como `Map<String, Object>`

Un `Map` ad-hoc es suficiente para un endpoint de diagnóstico. No justifica crear un DTO específico. Retorna `email` y `roles`.

### D3: Ruta `/api/test/**`

Agrupa ambos endpoints bajo un prefijo consistente. `/api/test/public` se añade al `requestMatchers(...).permitAll()` existente en `SecurityConfig`.

## Risks / Trade-offs

- **[Riesgo] Endpoint de diagnóstico en producción** → Aceptable porque `/api/test/ping` requiere token válido y no expone datos sensibles; `/api/test/public` solo retorna un string fijo. Si se desea, se puede proteger por perfil con `@Profile("!prod")`.
