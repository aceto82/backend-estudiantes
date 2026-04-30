## Context

The system uses JJWT 0.13 to issue short-lived access tokens. There is no persistence layer for token lifecycle management. `ddl-auto=update` means Hibernate creates new tables automatically — no migration tooling required. `Usuario` is the central identity entity; `Estudiante` and `Instructor` each reference it via `@OneToOne`. A refresh token is owned by a `Usuario` in a many-to-one relationship (one user may hold multiple active tokens across devices).

## Goals / Non-Goals

**Goals:**
- Define the `RefreshToken` entity schema: token string, owner, expiry, revoked flag, creation timestamp
- Provide a `RefreshTokenRepository` with the query methods needed by upcoming service logic

**Non-Goals:**
- Refresh token issuance or rotation logic — service layer, separate change
- `POST /api/auth/refresh` endpoint — controller layer, separate change
- Token cleanup / scheduled expiry sweeps — operational concern, separate change

## Decisions

### Token value: UUID string stored as VARCHAR

Refresh tokens are opaque random identifiers. `UUID.randomUUID().toString()` is cryptographically sufficient, universally unique, and requires no additional libraries. Storing as `VARCHAR(36)` with a unique constraint prevents collision and allows indexed lookup.

*Alternative considered*: Signed JWT as the refresh token. Rejected — JWTs are self-contained and stateless; storing them defeats the purpose of having a revocable, server-side record.

### Relationship: `@ManyToOne` to `Usuario`

A user may be logged in on multiple devices simultaneously, so each session holds its own refresh token. `@ManyToOne` with a non-nullable foreign key is the correct cardinality.

*Alternative considered*: `@OneToOne` (one token per user). Rejected — this would force single-session semantics and require extra logic to replace existing tokens on each login.

### Revocation: boolean `revoked` flag

Explicit revocation (logout, password change, admin action) flips this flag to `true` without deleting the row. This preserves audit history and makes it safe to query "was this token ever valid?".

*Alternative considered*: Hard-delete on revocation. Rejected — no audit trail; also complicates token reuse detection.

### Expiry: `Instant expiresAt`

`java.time.Instant` maps to a timezone-aware timestamp column in PostgreSQL (`TIMESTAMP WITH TIME ZONE`). The service layer will compare `Instant.now()` against this field. TTL value is injected via config (separate from JWT access token TTL).

## Risks / Trade-offs

- `ddl-auto=update` adds the `refresh_tokens` table automatically — safe in development; a migration script should be used before any production deployment.
- Accumulation of expired rows over time requires a cleanup job — acknowledged as a future operational concern.
- No index on `usuario_id` is defined explicitly; Hibernate adds a foreign key index by default on most PostgreSQL versions.

## Open Questions

- Should `RefreshTokenRepository` expose `deleteByUsuario(Usuario usuario)` now for future logout support, or wait until the logout endpoint change? Decision: include it — it costs nothing and avoids a second migration-like PR.
