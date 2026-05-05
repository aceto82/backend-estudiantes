## Why

Con el filtro JWT implementado, no existe ningún endpoint protegido que permita verificar rápidamente que la autenticación funciona end-to-end. Un controller dedicado de prueba proporciona rutas simples para confirmar que el token es aceptado, que el contexto de seguridad se carga correctamente, y que las rutas sin token devuelven 401/403, sin necesidad de datos de negocio.

## What Changes

- Se crea `TestController` en `controller/` con dos endpoints bajo `/api/test`:
  - `GET /api/test/ping` — protegido, retorna información del usuario autenticado (email y roles)
  - `GET /api/test/public` — público (permitAll), retorna un mensaje fijo para confirmar que el servidor responde sin token

## Capabilities

### New Capabilities

- `jwt-test-endpoints`: Endpoints de diagnóstico para verificar el filtro JWT y el flujo de autenticación end-to-end.

### Modified Capabilities

- `jwt-authentication-filter`: Se agrega permiso explícito para `/api/test/public` en `SecurityConfig`, manteniendo `/api/test/ping` protegido.

## Impact

- **Archivos nuevos:** `controller/TestController.java`
- **Archivos modificados:** `config/SecurityConfig.java` — agregar `/api/test/public` a las rutas permitidas
- **APIs nuevas:** `GET /api/test/ping` (auth requerida), `GET /api/test/public` (sin auth)
- **Solo para desarrollo/diagnóstico** — no expone datos de negocio sensibles
