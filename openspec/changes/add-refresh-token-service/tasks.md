## 1. Configuración

- [ ] 1.1 Agregar `app.refresh-token.expirationMs=604800000` en `application.properties`

## 2. DTO AuthTokensResponse

- [ ] 2.1 Crear `dto/AuthTokensResponse` con campos `accessToken` y `refreshToken` (Lombok `@Data @Builder @NoArgsConstructor @AllArgsConstructor`)

## 3. RefreshTokenService — implementación

- [ ] 3.1 Crear `service/RefreshTokenService` con `@Service` e inyectar `RefreshTokenRepository` y `@Value("${app.refresh-token.expirationMs}")`
- [ ] 3.2 Implementar `crear(Usuario usuario)`: UUID aleatorio, calcular `expiresAt`, construir entidad con builder, llamar `repository.save()`
- [ ] 3.3 Implementar `buscarPorToken(String token)`: llamar `findByToken()`, lanzar excepción si vacío, si expirado, o si `revoked = true`
- [ ] 3.4 Implementar `borrarPorUsuario(Usuario usuario)`: delegar en `repository.deleteByUsuario()`
- [ ] 3.5 Implementar `rotarToken(String tokenViejo)`: llamar `buscarPorToken()`, marcar `revoked = true` + `save()`, llamar `crear()`, devolver nuevo token
- [ ] 3.6 Implementar `limpiarTokensExpirados()`: llamar `repository.deleteByExpiresAtBefore(Instant.now())`

## 4. Actualizar AuthResponseBuilder

- [ ] 4.1 Cambiar firma de `buildLoginSuccess` a `(String email, String accessToken, String refreshToken)` y actualizar el `Map.of` para incluir `"accessToken"` y `"refreshToken"`

## 5. Actualizar AuthServices

- [ ] 5.1 Inyectar `RefreshTokenService` en `AuthServices`
- [ ] 5.2 Cambiar tipo de retorno de `authenticate()` a `AuthTokensResponse`
- [ ] 5.3 Después de validar credenciales, llamar `refreshTokenService.crear(usuario)` y construir el `AuthTokensResponse` con ambos tokens

## 6. Actualizar AuthController

- [ ] 6.1 Adaptar `login()` para consumir `AuthTokensResponse` y pasar `accessToken` y `refreshToken` a `AuthResponseBuilder.buildLoginSuccess`

## 7. Tests — RefreshTokenService

- [ ] 7.1 Crear `RefreshTokenServiceTest` con `@ExtendWith(MockitoExtension.class)` y repositorio mockeado
- [ ] 7.2 Test `crear`: `cuandoUsuarioValido_persisteTokenConCamposCorrectos`
- [ ] 7.3 Test `crear`: `cuandoSeLlamaDosVeces_generaTokensUnicos`
- [ ] 7.4 Test `buscarPorToken`: `cuandoTokenValido_devuelveEntidad`
- [ ] 7.5 Test `buscarPorToken`: `cuandoTokenNoExiste_lanzaExcepcion`
- [ ] 7.6 Test `buscarPorToken`: `cuandoTokenExpirado_lanzaExcepcion`
- [ ] 7.7 Test `buscarPorToken`: `cuandoTokenRevocado_lanzaExcepcion`
- [ ] 7.8 Test `borrarPorUsuario`: `cuandoUsuarioConTokens_delegaAlRepositorio`
- [ ] 7.9 Test `rotarToken`: `cuandoTokenValido_revocaViejoYCreaNuevo`
- [ ] 7.10 Test `rotarToken`: `cuandoTokenInvalido_lanzaExcepcionYNoCreaNuevo`
- [ ] 7.11 Test `limpiarTokensExpirados`: `delegaConInstanteCorrecto`

## 8. Tests — AuthResponseBuilder

- [ ] 8.1 Actualizar test `buildLoginSuccess` para verificar las tres claves: `email`, `accessToken`, `refreshToken`
