## Why

`AuthController` builds its login response inline with an ad-hoc `Map.of("email", ..., "token", ...)`. This is scattered, hard to test in isolation, and inconsistent with what the `utils/` package was scaffolded for. A dedicated utility centralises response construction and makes the shape of auth responses explicit and reusable.

## What Changes

- Add `AuthResponseBuilder` utility class in `utils/` with a static `buildLoginSuccess(String email, String token)` method that returns the standard auth response map
- Add `AuthResponseBuilder.buildError(String message)` for the error case, replacing the inline `Map.of("error", ...)` in `AuthController`
- Refactor `AuthController.login` to delegate both success and error response construction to `AuthResponseBuilder`

## Capabilities

### New Capabilities

- `auth-response-builder`: A utility that constructs standardised authentication response maps for success and error cases

### Modified Capabilities

<!-- No spec-level behavior changes — the response shape and HTTP status codes remain the same -->

## Impact

- `utils/AuthResponseBuilder.java` — new class
- `AuthController` — uses `AuthResponseBuilder` instead of inline `Map.of(...)`, logic unchanged
- No API contract changes (same response keys, same status codes)
- No new dependencies
