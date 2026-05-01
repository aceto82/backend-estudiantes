## 1. Nuevo DTO RefreshTokenRequest

- [ ] 1.1 Crear `dto/RefreshTokenRequest` con campo `@NotBlank(message = "El refresh token es obligatorio") String refreshToken` (Lombok `@Data @NoArgsConstructor`)

## 2. Refactorizar RefreshTokenService

- [ ] 2.1 Agregar método `rotar(RefreshToken tokenViejo)`: marcar `revoked = true`, llamar `save()`, llamar `crear(tokenViejo.getUsuario())`, devolver nuevo `RefreshToken`
- [ ] 2.2 Refactorizar `rotarToken(String tokenViejo)` para que delegue en `rotar(buscarPorToken(tokenViejo))`

## 3. Actualizar AuthResponseBuilder

- [ ] 3.1 Agregar método estático `buildRefreshSuccess(String accessToken, String refreshToken, long expiresIn)` que devuelva Map con `"accessToken"`, `"refreshToken"`, `"tokenType"` (TOKEN_TYPE), `"expiresIn"`

## 4. Actualizar AuthController

- [ ] 4.1 Inyectar `RefreshTokenService` y `JwtService` en `AuthController`
- [ ] 4.2 Agregar endpoint `POST /api/auth/refresh-token` con `@Valid @RequestBody RefreshTokenRequest`
- [ ] 4.3 Implementar el flujo: `buscarPorToken` → `rotar` → `generateToken` → `getExpirationMs()/1000` → `buildRefreshSuccess`
- [ ] 4.4 Manejar excepciones con `ResponseEntity.badRequest().body(AuthResponseBuilder.buildError(e.getMessage()))`

## 5. Tests — RefreshTokenService

- [ ] 5.1 Test `rotar`: `cuandoEntidadValida_revocaViejoYDevuelveNuevoToken`
- [ ] 5.2 Test `rotarToken`: `cuandoStringValido_delegaEnRotarYDevuelveNuevoToken` (verifica que sigue funcionando como wrapper)

## 6. Tests — AuthResponseBuilder

- [ ] 6.1 Test `buildRefreshSuccess_contieneCuatroClavesSinUsuario`: verifica las 4 claves, `tokenType = "Bearer"`, ausencia de `"usuario"` y valor de `"expiresIn"`
