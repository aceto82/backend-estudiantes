## 1. Fix JwtService bug

- [ ] 1.1 In `JwtService.extractAllClaims`, replace `parseClaimsJwt` with `parseClaimsJws`

## 2. Implement token generation in JwtService

- [ ] 2.1 Add `generateToken(UserDetails userDetails)` method that delegates to the overload with no extra claims
- [ ] 2.2 Add `generateToken(Map<String, Object> extraClaims, UserDetails userDetails)` method that builds a signed JWT with subject, iat, exp, and extra claims using `Jwts.builder()`

## 3. Wire token generation into AuthServices

- [ ] 3.1 Inject `JwtService` into `AuthServices`
- [ ] 3.2 After `authenticate()` validates credentials, call `jwtService.generateToken(usuario)` and return the token string to the caller

## 4. Update AuthController login response

- [ ] 4.1 Update `AuthController.login` to receive the token from `AuthServices` and include it in the response map under key `"token"`
- [ ] 4.2 Remove the `"message"` field from the response (replaced by the presence of a token)

## 5. Test

- [ ] 5.1 Write a unit test for `JwtService.generateToken` verifying subject, expiry, and `isTokenValid` round-trip
- [ ] 5.2 Write a unit test for `JwtService.extractAllClaims` verifying it no longer throws on a signed token
- [ ] 5.3 Verify `POST /api/auth/login` returns a `token` field with valid credentials (manual or integration test)
