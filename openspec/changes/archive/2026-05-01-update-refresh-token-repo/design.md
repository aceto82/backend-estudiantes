## Context

`RefreshTokenRepository` currently has `findByToken` and `deleteByUsuario`. The upcoming refresh service needs to list a user's tokens and the cleanup job needs to purge expired ones. Both additions are pure repository concerns — no schema change, no new Spring beans.

## Goals / Non-Goals

**Goals:**
- Expose `findByUsuario` for session-aware service logic
- Expose `deleteByExpiresAtBefore(Instant)` for expired-token cleanup

**Non-Goals:**
- Scheduling the cleanup — that's a separate operational change
- Any service or controller logic — repository layer only

## Decisions

### `findByUsuario` — derived query, returns `List`

Spring Data derives `SELECT * FROM refresh_tokens WHERE usuario_id = ?` from the method name. No `@Query` needed. Return type is `List<RefreshToken>` because one user can have multiple tokens across devices.

### `deleteByExpiresAtBefore(Instant now)` — derived delete with explicit `Instant` parameter

Spring Data derives `DELETE FROM refresh_tokens WHERE expires_at < ?` from the method name. The `Instant` is passed by the caller (e.g., `Instant.now()`), not hardcoded in the query.

*Alternative considered*: `@Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < CURRENT_TIMESTAMP")` with no parameter. Rejected — `CURRENT_TIMESTAMP` in JPQL is not reliably portable to H2 in tests, and passing `Instant.now()` explicitly makes the method trivially testable with a controlled timestamp.

*Alternative considered*: `@Query` with `:now` parameter. Rejected — the derived delete is simpler, follows the existing `deleteByUsuario` pattern, and requires no annotation.

## Risks / Trade-offs

- Derived delete methods execute a `SELECT` followed by individual `DELETE`s by default in Spring Data JPA (not a bulk `DELETE` statement). For large tables this is inefficient. Mitigated by: at this stage the table is small; if performance becomes a concern, replace with `@Modifying @Query(...)` in a follow-up.
- `@Transactional` is required on `deleteByExpiresAtBefore` for the same reason as `deleteByUsuario`.
