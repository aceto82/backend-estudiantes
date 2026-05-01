## ADDED Requirements

### Requirement: RefreshToken repository lookup by user
The system SHALL provide `RefreshTokenRepository.findByUsuario(Usuario usuario)` returning `List<RefreshToken>`, enabling service-layer retrieval of all tokens owned by a given user (e.g., for session listing or pre-logout auditing).

#### Scenario: Returns all tokens for a user
- **WHEN** `findByUsuario` is called with a `Usuario` that has multiple refresh tokens
- **THEN** a list containing all tokens owned by that user is returned

#### Scenario: Returns empty list when user has no tokens
- **WHEN** `findByUsuario` is called with a `Usuario` that has no refresh tokens
- **THEN** an empty list is returned

#### Scenario: Does not return tokens belonging to other users
- **WHEN** `findByUsuario` is called for user A while user B also has tokens in the database
- **THEN** only user A's tokens are included in the result

### Requirement: RefreshToken repository deletion of expired tokens
The system SHALL provide `RefreshTokenRepository.deleteByExpiresAtBefore(Instant now)` to bulk-delete all tokens whose `expiresAt` timestamp is strictly before the provided `Instant`, enabling on-demand or scheduled cleanup of expired tokens.

#### Scenario: Deletes only expired tokens
- **WHEN** `deleteByExpiresAtBefore(Instant.now())` is called and both expired and non-expired tokens exist
- **THEN** only tokens with `expiresAt` before `now` are deleted
- **THEN** tokens with `expiresAt` at or after `now` remain in the database

#### Scenario: No-op when no expired tokens exist
- **WHEN** `deleteByExpiresAtBefore` is called and all tokens have a future `expiresAt`
- **THEN** no records are deleted and no error is thrown
