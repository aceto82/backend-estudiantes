## Why

The login endpoint authenticates users successfully but returns no JWT token — the client has no credential to attach to subsequent requests. `JwtService` can parse and validate tokens but cannot produce them, making the auth flow incomplete.

## What Changes

- Add `generateToken(UserDetails)` and `generateToken(Map<String, Object>, UserDetails)` methods to `JwtService`
- Wire `JwtService` into `AuthServices` so login produces a token
- Update `AuthController` login response to include the JWT token instead of a plain success message
- Fix `parseClaimsJwt` → `parseClaimsJws` bug in `JwtService.extractAllClaims` (signed tokens require `parseClaimsJws`)

## Capabilities

### New Capabilities

- `jwt-token-generation`: Generating signed JWT tokens from a `UserDetails` principal, including subject, issued-at, expiration, and optional extra claims

### Modified Capabilities

<!-- No existing specs to modify -->

## Impact

- `JwtService` — new public methods
- `AuthServices` — gains a `JwtService` dependency; `authenticate()` return type changes (or new method added) to produce a token alongside the authenticated user
- `AuthController` — login response body gains a `token` field
- No new dependencies required (JJWT 0.13 already present)