## 1. Agregar métodos al repositorio

- [x] 1.1 Agregar `List<RefreshToken> findByUsuario(Usuario usuario)` a `RefreshTokenRepository`
- [x] 1.2 Agregar `@Transactional void deleteByExpiresAtBefore(Instant now)` a `RefreshTokenRepository`

## 2. Tests

- [x] 2.1 Test `findByUsuario` retorna todos los tokens del usuario indicado
- [x] 2.2 Test `findByUsuario` retorna lista vacía si el usuario no tiene tokens
- [x] 2.3 Test `findByUsuario` no incluye tokens de otros usuarios
- [x] 2.4 Test `deleteByExpiresAtBefore` elimina solo los tokens expirados y deja intactos los vigentes
- [x] 2.5 Test `deleteByExpiresAtBefore` no lanza error cuando no hay tokens expirados
