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
