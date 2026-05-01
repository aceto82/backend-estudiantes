## Requirements

### Requirement: RefreshToken entity schema
The system SHALL provide a `RefreshToken` JPA entity mapped to the `refresh_tokens` table with the following fields: `id` (auto-generated Long primary key), `token` (unique VARCHAR UUID string, non-nullable), `usuario` (non-nullable `@ManyToOne` to `Usuario`), `expiresAt` (`Instant`, non-nullable), `revoked` (boolean, default `false`, non-nullable), and `createdAt` (`Instant`, non-nullable, set at creation).

#### Scenario: Entity persists with required fields
- **WHEN** a `RefreshToken` is saved with a valid UUID token, an existing `Usuario`, a future `expiresAt`, and `revoked = false`
- **THEN** the record is persisted to the `refresh_tokens` table with all fields stored correctly

#### Scenario: Token column is unique
- **WHEN** two `RefreshToken` records are saved with the same token string
- **THEN** a database constraint violation is thrown

### Requirement: RefreshToken repository lookup by token
The system SHALL provide `RefreshTokenRepository.findByToken(String token)` returning `Optional<RefreshToken>`, enabling service-layer validation of an incoming refresh token string.

#### Scenario: Token found
- **WHEN** `findByToken` is called with a token string that exists in the database
- **THEN** an `Optional` containing the matching `RefreshToken` is returned

#### Scenario: Token not found
- **WHEN** `findByToken` is called with a token string that does not exist
- **THEN** an empty `Optional` is returned

### Requirement: RefreshToken repository deletion by user
The system SHALL provide `RefreshTokenRepository.deleteByUsuario(Usuario usuario)` to bulk-revoke all refresh tokens belonging to a user (used during logout and password change flows).

#### Scenario: Tokens deleted for user
- **WHEN** `deleteByUsuario` is called with a `Usuario` that has existing refresh token records
- **THEN** all `RefreshToken` records associated with that user are removed from the database

#### Scenario: No-op when user has no tokens
- **WHEN** `deleteByUsuario` is called with a `Usuario` that has no refresh token records
- **THEN** no error is thrown and the database is unchanged

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
