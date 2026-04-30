## ADDED Requirements

### Requirement: Build successful auth response
The system SHALL provide `AuthResponseBuilder.buildLoginSuccess(String email, String token)` that returns a `Map<String, Object>` containing exactly the keys `"email"` and `"token"` with their respective values.

#### Scenario: Success map contains email and token
- **WHEN** `buildLoginSuccess("user@example.com", "eyJ...")` is called
- **THEN** the returned map contains key `"email"` with value `"user@example.com"`
- **THEN** the returned map contains key `"token"` with value `"eyJ..."`
- **THEN** the map contains exactly 2 entries

### Requirement: Build error auth response
The system SHALL provide `AuthResponseBuilder.buildError(String message)` that returns a `Map<String, Object>` containing exactly the key `"error"` with the provided message as its value.

#### Scenario: Error map contains error message
- **WHEN** `buildError("Contraseña incorrecta")` is called
- **THEN** the returned map contains key `"error"` with value `"Contraseña incorrecta"`
- **THEN** the map contains exactly 1 entry

### Requirement: AuthController uses AuthResponseBuilder
The system SHALL use `AuthResponseBuilder` in `AuthController.login` for both the success and error response bodies, replacing all inline `Map.of(...)` construction.

#### Scenario: Successful login delegates to builder
- **WHEN** `POST /api/auth/login` is called with valid credentials
- **THEN** the response body is the map produced by `AuthResponseBuilder.buildLoginSuccess`

#### Scenario: Failed login delegates to builder
- **WHEN** `POST /api/auth/login` is called with invalid credentials
- **THEN** the response body is the map produced by `AuthResponseBuilder.buildError`
