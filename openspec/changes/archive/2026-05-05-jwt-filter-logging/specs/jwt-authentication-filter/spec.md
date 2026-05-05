## ADDED Requirements

### Requirement: Log requests without Authorization header
The system SHALL emit a DEBUG log in `JwtAuthenticationFilter` when a request arrives without an `Authorization` header or with a non-Bearer value, before passing the request through the chain.

#### Scenario: Missing header is logged at DEBUG
- **WHEN** a request arrives with no `Authorization` header
- **THEN** a DEBUG log entry is emitted indicating the request has no Bearer token

### Requirement: Log successful authentication
The system SHALL emit an INFO log when a JWT token is validated successfully and the authentication is set in the `SecurityContextHolder`. The log SHALL include the authenticated user's email.

#### Scenario: Valid token logs authentication at INFO
- **WHEN** a request with a valid Bearer token is processed
- **AND** `jwtService.isTokenValid` returns true
- **THEN** an INFO log entry is emitted with the authenticated user's email
- **THEN** the log does NOT include the raw JWT token string

### Requirement: Log invalid or expired tokens as warnings
The system SHALL emit a WARN log when an exception is caught during token parsing or validation (expired, malformed, signature invalid). The log SHALL include the exception message but NOT the full token.

#### Scenario: Expired token logs a warning
- **WHEN** a request with an expired Bearer token is processed
- **AND** `jwtService.extractUsername` or `jwtService.isTokenValid` throws an exception
- **THEN** a WARN log entry is emitted with the exception message
- **THEN** the log does NOT include the raw JWT token string

### Requirement: Log user not found as warning
The system SHALL emit a WARN log when `UsuarioDetailsService.loadUserByUsername` throws `UsernameNotFoundException` during token processing.

#### Scenario: Unknown user subject logs a warning
- **WHEN** a request arrives with a token whose subject does not match any user
- **THEN** a WARN log entry is emitted indicating the user was not found
