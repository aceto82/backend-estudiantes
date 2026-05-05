## Why

El `JwtAuthenticationFilter` actual no registra ningún evento, lo que hace imposible trazar problemas de autenticación en producción (tokens rechazados, usuarios no encontrados, errores silenciosos). Agregar logs estructurados permite diagnosticar fallos sin necesidad de un debugger.

## What Changes

- Se añaden logs en `JwtAuthenticationFilter` para los eventos relevantes del flujo de validación:
  - Solicitud sin header `Authorization` (DEBUG)
  - Token extraído correctamente (DEBUG)
  - Usuario cargado y token válido → autenticación establecida (INFO)
  - Token inválido o expirado capturado en el catch (WARN)
  - Usuario no encontrado (`UsernameNotFoundException`) (WARN)

## Capabilities

### New Capabilities

_(ninguna)_

### Modified Capabilities

- `jwt-authentication-filter`: Se añaden requisitos de logging para los eventos del flujo de validación del token.

## Impact

- **Archivos modificados:** `filter/JwtAuthenticationFilter.java`
- **Dependencias nuevas:** ninguna (Lombok `@Slf4j` ya disponible en el proyecto)
- **APIs:** sin cambios en comportamiento externo
