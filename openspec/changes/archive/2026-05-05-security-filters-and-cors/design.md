## Context

El `SecurityConfig` actual configura permisos por ruta pero no valida tokens JWT en ninguna solicitud — `JwtService` existe y puede generar/validar tokens, `UsuarioDetailsService` puede cargar usuarios por email, pero ningún filtro conecta ambas piezas al ciclo de vida de una solicitud HTTP. El directorio `filter/` está vacío. Sin un filtro JWT, cualquier token enviado en el header `Authorization` es ignorado. Sin CORS, el navegador bloquea las solicitudes del frontend.

## Goals / Non-Goals

**Goals:**
- Interceptar cada solicitud entrante, extraer el JWT del header `Authorization: Bearer <token>` y, si es válido, establecer la autenticación en el `SecurityContextHolder`.
- Configurar CORS en Spring Security para permitir el origen del frontend, los métodos necesarios y los headers `Authorization` y `Content-Type`.
- Registrar el filtro en la cadena de seguridad antes de `UsernamePasswordAuthenticationFilter`.
- Establecer política de sesión `STATELESS` para no crear sesiones HTTP.

**Non-Goals:**
- Manejo de roles/autorización por endpoint (ya existe en `SecurityConfig`).
- Renovación o revocación de tokens (cubierto por `refresh-token-*` specs).
- Rate limiting o throttling de solicitudes.

## Decisions

### D1: `OncePerRequestFilter` como base del filtro JWT

`OncePerRequestFilter` garantiza ejecución una sola vez por solicitud incluso en reenvíos internos de Spring. Es la convención estándar para filtros de autenticación en Spring Security. Alternativa descartada: implementar `Filter` directamente, que requeriría gestionar la invocación única manualmente.

### D2: Flujo del filtro — skip si no hay token

Si el header `Authorization` está ausente o no empieza con `"Bearer "`, el filtro llama `filterChain.doFilter` sin modificar el contexto. Spring Security rechazará la solicitud en la siguiente etapa si la ruta requiere autenticación. Esto es preferible a lanzar una excepción en el filtro, ya que permite que rutas públicas (`/api/auth/**`) pasen sin token.

### D3: CORS configurado vía `CorsConfigurationSource` bean + `httpSecurity.cors()`

Centralizar la configuración CORS en un bean `CorsConfigurationSource` registrado en Spring permite que tanto Spring Security como Spring MVC compartan la misma política. Alternativa descartada: `@CrossOrigin` en cada controlador — dispersa la configuración y no aplica a todos los endpoints uniformemente.

### D4: Origen del frontend como variable de entorno (`CORS_ALLOWED_ORIGIN`)

El origen del frontend varía entre entornos (local, staging, producción). Hardcodear `http://localhost:4200` solo funciona en desarrollo. Leer desde variable de entorno con valor por defecto `http://localhost:4200` mantiene flexibilidad sin requerir recompilación.

### D5: Política de sesión `STATELESS`

La autenticación es completamente stateless vía JWT. Crear sesiones HTTP sería overhead innecesario y podría introducir vulnerabilidades de fixation. `SessionCreationPolicy.STATELESS` instruye a Spring Security a no crear ni usar sesiones.

## Risks / Trade-offs

- **[Riesgo] Token inválido lanza excepción en `JwtService.extractAllClaims`** → Envolver la extracción en try-catch dentro del filtro; si falla, no establecer autenticación y dejar que Spring Security maneje el rechazo.
- **[Trade-off] `UsuarioDetailsService.loadUserByUsername` hace una consulta DB por solicitud autenticada** → Aceptable para el volumen actual; si escala, se puede agregar caché de `UserDetails`. No se aborda en este cambio.
- **[Riesgo] CORS demasiado permisivo si `CORS_ALLOWED_ORIGIN=*`** → No se permite `*` con `allowCredentials(true)`; Spring lanzará error de configuración. El origen debe ser explícito.

## Migration Plan

1. Crear `JwtAuthenticationFilter` en `filter/`.
2. Actualizar `SecurityConfig`: agregar bean `CorsConfigurationSource`, habilitar `cors()`, agregar `sessionManagement(STATELESS)`, registrar el filtro antes de `UsernamePasswordAuthenticationFilter`.
3. Agregar `CORS_ALLOWED_ORIGIN` a las variables de entorno documentadas.
4. Verificar con prueba de integración manual: solicitud con token válido a ruta protegida → 200; sin token → 401; origen correcto en CORS → sin bloqueo.

Rollback: revertir `SecurityConfig` a versión anterior y eliminar `JwtAuthenticationFilter`.

## Open Questions

- ¿El frontend corre en un puerto fijo (ej. 4200 para Angular, 3000 para React)? Determina el valor por defecto de `CORS_ALLOWED_ORIGIN`.
- ¿Se necesita exponer el header `Authorization` en la respuesta (`exposedHeaders`) para que el frontend pueda leerlo?