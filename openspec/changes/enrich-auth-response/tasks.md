## 1. Nuevo DTO UsuarioInfo

- [ ] 1.1 Crear `dto/UsuarioInfo` con campos `Long id`, `String email`, `Role rol`, `String nombre`, `String apellido` (Lombok `@Data @Builder @NoArgsConstructor @AllArgsConstructor`)

## 2. Enriquecer AuthTokensResponse

- [ ] 2.1 Agregar campo `UsuarioInfo usuario` a `dto/AuthTokensResponse`
- [ ] 2.2 Agregar campo `long expiresIn` a `dto/AuthTokensResponse`

## 3. Exponer TTL en JwtService

- [ ] 3.1 Agregar método público `getExpirationMs()` en `JwtService` que devuelva `jwtExpiration`

## 4. Actualizar AuthServices

- [ ] 4.1 Construir `UsuarioInfo` mapeando desde el `Usuario` cargado (`id`, `email`, `rol`, `nombre`, `apellido`)
- [ ] 4.2 Calcular `expiresIn = jwtService.getExpirationMs() / 1000`
- [ ] 4.3 Incluir `usuario` y `expiresIn` en el `AuthTokensResponse` devuelto

## 5. Actualizar AuthResponseBuilder

- [ ] 5.1 Agregar constante `private static final String TOKEN_TYPE = "Bearer"`
- [ ] 5.2 Cambiar firma a `buildLoginSuccess(UsuarioInfo usuario, String accessToken, String refreshToken, long expiresIn)`
- [ ] 5.3 Construir y devolver Map con claves: `"usuario"` (Map de 5 campos), `"accessToken"`, `"refreshToken"`, `"tokenType"`, `"expiresIn"`

## 6. Actualizar AuthController

- [ ] 6.1 Pasar `tokens.getUsuario()`, `tokens.getAccessToken()`, `tokens.getRefreshToken()`, `tokens.getExpiresIn()` a `buildLoginSuccess`

## 7. Tests — AuthResponseBuilder

- [ ] 7.1 Actualizar `buildLoginSuccess_contieneEmailAccessTokenYRefreshToken` → renombrar y reescribir para verificar las 5 claves raíz y ausencia de `"email"` top-level
- [ ] 7.2 Test `buildLoginSuccess_usuarioContieneLosCincoSubcampos`: verifica id, email, rol, nombre, apellido dentro del sub-mapa `"usuario"`
- [ ] 7.3 Test `buildLoginSuccess_tokenTypeEsBearer`: verifica que `"tokenType"` sea `"Bearer"`
- [ ] 7.4 Test `buildLoginSuccess_expiresInRefleja3600`: verifica que `"expiresIn"` sea el valor pasado
