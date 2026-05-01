## 1. Nuevo DTO RefreshTokenRequest

- [x] 1.1 Crear `dto/RefreshTokenRequest` con campo `@NotBlank(message = "El refresh token es obligatorio") String refreshToken` (Lombok `@Data @NoArgsConstructor`)

## 2. Refactorizar RefreshTokenService

- [x] 2.1 Agregar método `rotar(RefreshToken tokenViejo)`: marcar `revoked = true`, llamar `save()`, llamar `crear(tokenViejo.getUsuario())`, devolver nuevo `RefreshToken`
- [x] 2.2 Refactorizar `rotarToken(String tokenViejo)` para que delegue en `rotar(buscarPorToken(tokenViejo))`

## 3. Actualizar AuthResponseBuilder

- [x] 3.1 Agregar método estático `buildRefreshSuccess(String accessToken, String refreshToken, long expiresIn)` que devuelva Map con `"accessToken"`, `"refreshToken"`, `"tokenType"` (TOKEN_TYPE), `"expiresIn"`

## 4. Actualizar AuthController

- [x] 4.1 Inyectar `RefreshTokenService` y `JwtService` en `AuthController`
- [x] 4.2 Agregar endpoint `POST /api/auth/refresh-token` con `@Valid @RequestBody RefreshTokenRequest`
- [x] 4.3 Implementar el flujo: `buscarPorToken` → `rotar` → `generateToken` → `getExpirationMs()/1000` → `buildRefreshSuccess`
- [x] 4.4 Manejar excepciones con `ResponseEntity.badRequest().body(AuthResponseBuilder.buildError(e.getMessage()))`

## 5. Tests — RefreshTokenService

- [x] 5.1 Test `rotar`: `cuandoEntidadValida_revocaViejoYDevuelveNuevoToken`
- [x] 5.2 Test `rotarToken`: `cuandoStringValido_delegaEnRotarYDevuelveNuevoToken` (verifica que sigue funcionando como wrapper)

## 6. Tests — AuthResponseBuilder

- [x] 6.1 Test `buildRefreshSuccess_contieneCuatroClavesSinUsuario`: verifica las 4 claves, `tokenType = "Bearer"`, ausencia de `"usuario"` y valor de `"expiresIn"`
