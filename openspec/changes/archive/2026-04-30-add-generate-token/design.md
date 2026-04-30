## Context

`JwtService` already handles token parsing and validation using JJWT 0.13. The secret key and expiration TTL are injected via `@Value` from environment variables (`JWT_SECRET`, `JWT_EXPIRATION`). `AuthServices.authenticate()` returns a `Usuario` on success but does not produce a token. The controller then assembles a plain response map.

The fix is contained to three classes: `JwtService`, `AuthServices`, and `AuthController`. No schema changes, no new dependencies.

## Goals / Non-Goals

**Goals:**
- `JwtService.generateToken` builds a signed HS256 JWT with subject = email, iat, exp, and optional extra claims
- Login response includes the generated token under a `token` key
- Fix the `parseClaimsJwt` → `parseClaimsJws` bug so token validation works correctly for signed tokens

**Non-Goals:**
- Refresh tokens — out of scope for this change
- JWT filter / request authentication — next step, separate change
- Role claims embedded in the token — not required yet; roles are loaded from DB on each request via `UserDetailsService`

## Decisions

### Token generation sits in `JwtService`, not `AuthServices`

`JwtService` owns all JWT concerns (sign, parse, validate). Putting generation there is consistent. `AuthServices` calls `JwtService.generateToken` and returns the token string (or a DTO) to the controller.

*Alternative considered*: Controller calls `JwtService` directly after `authenticate()`. Rejected — keeps auth orchestration in the service layer.

### Return type of login: DTO vs `Map`

The controller currently returns `Map.of(...)`. Introducing a `LoginResponse` DTO is cleaner and type-safe, but the controller already uses `Map` — we keep `Map` to minimise scope. The DTO refactor belongs in a cleanup pass.

### `generateToken` overloads

Two overloads mirror the common JJWT pattern:
1. `generateToken(UserDetails)` — no extra claims, covers 99% of uses
2. `generateToken(Map<String, Object> extraClaims, UserDetails)` — allows callers to embed additional data (roles, tenant ID, etc.) without reimplementing the builder

### `parseClaimsJwt` → `parseClaimsJws` bug fix

`parseClaimsJwt` is for unsigned (plain) JWTs; signed tokens must use `parseClaimsJws`. This causes a `UnsupportedJwtException` at runtime. Fix it as part of this change since `generateToken` will immediately exercise the full sign-then-verify path.

## Risks / Trade-offs

- `RuntimeException` in `AuthServices` — the controller catches all exceptions and returns 400. Acceptable short-term; proper typed exceptions (`AuthException`, `InvalidCredentialsException`) belong in the `exceptions/` package but are out of scope here.
- `Map` response — no compile-time contract on the response shape; acceptable given the DTO refactor is acknowledged as out of scope.

## Open Questions

- Should the `rol` claim be embedded in the JWT to avoid a DB lookup in the future filter? Decision deferred to the JWT filter change.