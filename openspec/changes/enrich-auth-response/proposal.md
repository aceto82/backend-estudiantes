## Why

La respuesta actual de `POST /api/auth/login` devuelve solo el email y los tokens, obligando al cliente a hacer una segunda llamada para obtener los datos del usuario autenticado. Agregar la información del usuario, el tipo de token y el tiempo de expiración en la misma respuesta reduce las llamadas innecesarias y alinea el contrato con el estándar OAuth2.

## What Changes

- **BREAKING** `AuthResponseBuilder.buildLoginSuccess` cambia de firma: elimina el parámetro `email` top-level, agrega `UsuarioInfo usuario` y `long expiresIn`; la respuesta JSON quita la clave `"email"` y agrega `"usuario"`, `"tokenType"` y `"expiresIn"`
- Nuevo `dto/UsuarioInfo` con los campos públicos del usuario: `id`, `email`, `rol`, `nombre`, `apellido`
- `dto/AuthTokensResponse` incorpora `UsuarioInfo usuario` y `long expiresIn`
- `JwtService` expone `getExpirationMs()` para que la capa de servicio pueda calcular `expiresIn`
- `AuthServices.authenticate()` mapea el `Usuario` a `UsuarioInfo` y calcula `expiresIn` en segundos

## Capabilities

### New Capabilities

- `usuario-info`: DTO de representación pública del usuario para respuestas de autenticación

### Modified Capabilities

- `auth-response-builder`: La firma de `buildLoginSuccess` cambia completamente — elimina `email`, agrega `UsuarioInfo`, `expiresIn`; el contrato de la respuesta JSON cambia

## Impact

- `dto/UsuarioInfo.java` — archivo nuevo
- `dto/AuthTokensResponse.java` — dos campos nuevos: `usuario` y `expiresIn`
- `service/JwtService.java` — getter público `getExpirationMs()`
- `service/AuthServices.java` — mapeo a `UsuarioInfo` y cálculo de `expiresIn`
- `utils/AuthResponseBuilder.java` — nueva firma, constante `TOKEN_TYPE`, estructura de mapa actualizada
- `controller/AuthController.java` — pasa los nuevos argumentos al builder
- `test/utils/AuthResponseBuilderTest.java` — tests actualizados para el nuevo contrato
- Sin cambios en dependencias Maven ni en la base de datos
