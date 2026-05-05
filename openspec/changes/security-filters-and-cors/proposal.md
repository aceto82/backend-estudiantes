## Why

El `SecurityConfig` actual no tiene un filtro JWT conectado a la cadena de seguridad ni configuración CORS, por lo que las rutas protegidas no validan tokens y el frontend no puede hacer solicitudes cross-origin. Esta es la pieza que cierra el flujo de autenticación JWT iniciado en cambios anteriores.

## What Changes

- Se crea `JwtAuthenticationFilter` en `filter/` que extiende `OncePerRequestFilter`: extrae el token del header `Authorization`, lo valida con `JwtService` y establece el `SecurityContextHolder` si es válido.
- Se agrega configuración CORS en `SecurityConfig` que permite el origen del frontend, los métodos HTTP necesarios y los headers `Authorization` y `Content-Type`.
- Se registra el filtro en la cadena de seguridad antes de `UsernamePasswordAuthenticationFilter`.
- Se configura la política de sesión como `STATELESS` (sin sesiones HTTP).

## Capabilities

### New Capabilities

- `jwt-authentication-filter`: Filtro que intercepta cada solicitud, extrae y valida el JWT, y autentica al usuario en el contexto de seguridad de Spring.
- `cors-configuration`: Configuración CORS global que permite solicitudes del frontend definiendo orígenes, métodos y headers permitidos.

### Modified Capabilities

- `jwt-token-generation`: La spec existente cubre generación y validación de tokens; no cambian sus requisitos funcionales, solo se consume desde el nuevo filtro.

## Impact

- **Archivos modificados:** `config/SecurityConfig.java`
- **Archivos nuevos:** `filter/JwtAuthenticationFilter.java`
- **Dependencias existentes usadas:** `JwtService`, `UsuarioDetailsService`
- **Variables de entorno:** sin cambios (el secreto JWT ya está configurado)
- **APIs:** todas las rutas protegidas pasarán a requerir `Authorization: Bearer <token>` válido