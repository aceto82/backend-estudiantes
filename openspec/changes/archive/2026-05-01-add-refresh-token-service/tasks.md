## 1. Configuración

- [x] 1.1 Agregar `app.refresh-token.expirationMs=604800000` en `application.properties`

## 2. DTO AuthTokensResponse

- [x] 2.1 Crear `dto/AuthTokensResponse` con campos `accessToken` y `refreshToken` (Lombok `@Data @Builder @NoArgsConstructor @AllArgsConstructor`)

## 3. RefreshTokenService — implementación

- [x] 3.1 Crear `service/RefreshTokenService` con `@Service` e inyectar `RefreshTokenRepository` y `@Value("${app.refresh-token.expirationMs}")`
- [x] 3.2 Implementar `crear(Usuario usuario)`: UUID aleatorio, calcular `expiresAt`, construir entidad con builder, llamar `repository.save()`
- [x] 3.3 Implementar `buscarPorToken(String token)`: llamar `findByToken()`, lanzar excepción si vacío, si expirado, o si `revoked = true`
- [x] 3.4 Implementar `borrarPorUsuario(Usuario usuario)`: delegar en `repository.deleteByUsuario()`
- [x] 3.5 Implementar `rotarToken(String tokenViejo)`: llamar `buscarPorToken()`, marcar `revoked = true` + `save()`, llamar `crear()`, devolver nuevo token
- [x] 3.6 Implementar `limpiarTokensExpirados()`: llamar `repository.deleteByExpiresAtBefore(Instant.now())`

## 4. Actualizar AuthResponseBuilder

- [x] 4.1 Cambiar firma de `buildLoginSuccess` a `(String email, String accessToken, String refreshToken)` y actualizar el `Map.of` para incluir `"accessToken"` y `"refreshToken"`

## 5. Actualizar AuthServices

- [x] 5.1 Inyectar `RefreshTokenService` en `AuthServices`
- [x] 5.2 Cambiar tipo de retorno de `authenticate()` a `AuthTokensResponse`
- [x] 5.3 Después de validar credenciales, llamar `refreshTokenService.crear(usuario)` y construir el `AuthTokensResponse` con ambos tokens

## 6. Actualizar AuthController

- [x] 6.1 Adaptar `login()` para consumir `AuthTokensResponse` y pasar `accessToken` y `refreshToken` a `AuthResponseBuilder.buildLoginSuccess`

## 7. Tests — RefreshTokenService

- [x] 7.1 Crear `RefreshTokenServiceTest` con `@ExtendWith(MockitoExtension.class)` y repositorio mockeado
- [x] 7.2 Test `crear`: `cuandoUsuarioValido_persisteTokenConCamposCorrectos`
- [x] 7.3 Test `crear`: `cuandoSeLlamaDosVeces_generaTokensUnicos`
- [x] 7.4 Test `buscarPorToken`: `cuandoTokenValido_devuelveEntidad`
- [x] 7.5 Test `buscarPorToken`: `cuandoTokenNoExiste_lanzaExcepcion`
- [x] 7.6 Test `buscarPorToken`: `cuandoTokenExpirado_lanzaExcepcion`
- [x] 7.7 Test `buscarPorToken`: `cuandoTokenRevocado_lanzaExcepcion`
- [x] 7.8 Test `borrarPorUsuario`: `cuandoUsuarioConTokens_delegaAlRepositorio`
- [x] 7.9 Test `rotarToken`: `cuandoTokenValido_revocaViejoYCreaNuevo`
- [x] 7.10 Test `rotarToken`: `cuandoTokenInvalido_lanzaExcepcionYNoCreaNuevo`
- [x] 7.11 Test `limpiarTokensExpirados`: `delegaConInstanteCorrecto`

## 8. Tests — AuthResponseBuilder

- [x] 8.1 Actualizar test `buildLoginSuccess` para verificar las tres claves: `email`, `accessToken`, `refreshToken`
