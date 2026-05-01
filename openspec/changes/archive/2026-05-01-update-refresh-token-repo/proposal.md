## Why

`RefreshTokenRepository` currently supports only lookup by token string and deletion by user. Upcoming service logic (refresh flow, session listing, and expired-token cleanup) requires two additional query methods that the repository does not yet expose.

## What Changes

- Add `List<RefreshToken> findByUsuario(Usuario usuario)` to `RefreshTokenRepository` — returns all tokens owned by a user, enabling session listing and pre-deletion auditing
- Add `void deleteByExpiresAtBefore(Instant now)` to `RefreshTokenRepository` — bulk-deletes all tokens whose expiry timestamp is in the past, enabling scheduled or on-demand cleanup

## Capabilities

### New Capabilities

<!-- No new capabilities — both methods extend an existing repository -->

### Modified Capabilities

- `refresh-token-entity`: Adding two new repository query requirements to the existing capability

## Impact

- `repository/RefreshTokenRepository.java` — two new method signatures
- No entity, service, or controller changes in this change
- No new dependencies
