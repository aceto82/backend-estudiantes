## Why

The current login flow issues a single short-lived JWT access token. When it expires, the user must re-authenticate with their credentials. A refresh token mechanism allows clients to obtain a new access token silently; this change lays the persistence layer — the `RefreshToken` entity and repository — that all subsequent refresh-token features will build on.

## What Changes

- Add `RefreshToken` JPA entity to `model/` with fields: token string (UUID), owning `Usuario`, expiry timestamp, revoked flag, and creation timestamp
- Add `RefreshTokenRepository` to `repository/` exposing lookups by token string and by user
- Schema updated automatically via `ddl-auto=update` (no migration script needed)

## Capabilities

### New Capabilities

- `refresh-token-entity`: Persistent representation of a refresh token — its schema, lifecycle fields, and repository queries

### Modified Capabilities

<!-- No existing spec-level behavior changes -->

## Impact

- `model/RefreshToken.java` — new JPA entity, new table `refresh_tokens`
- `repository/RefreshTokenRepository.java` — new Spring Data repository
- No controller, service, or API surface changes in this change
- No new dependencies (Spring Data JPA already present)
