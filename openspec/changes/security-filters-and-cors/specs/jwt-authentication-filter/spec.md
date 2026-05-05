## ADDED Requirements

### Requirement: Extract JWT from Authorization header
The system SHALL implement `JwtAuthenticationFilter` extending `OncePerRequestFilter` that reads the `Authorization` header on every request. If the header is absent or does not start with `"Bearer "`, the filter SHALL pass the request through the chain without modification and without throwing an exception.

#### Scenario: Request without Authorization header passes through
- **WHEN** a request arrives with no `Authorization` header
- **THEN** the filter calls `filterChain.doFilter` without modifying the `SecurityContextHolder`

#### Scenario: Request with non-Bearer Authorization passes through
- **WHEN** a request arrives with `Authorization: Basic dXNlcjpwYXNz`
- **THEN** the filter calls `filterChain.doFilter` without modifying the `SecurityContextHolder`

#### Scenario: Request with Bearer token is processed
- **WHEN** a request arrives with `Authorization: Bearer <token>`
- **THEN** the filter extracts `<token>` and attempts validation

### Requirement: Validate JWT and authenticate in SecurityContext
The system SHALL use `JwtService.extractUsername(token)` to obtain the email, then call `UsuarioDetailsService.loadUserByUsername(email)` to load the user, then call `JwtService.isTokenValid(token, userDetails)` to verify the token. If valid and no authentication is already set in the `SecurityContextHolder`, the system SHALL create a `UsernamePasswordAuthenticationToken` with the `UserDetails`, set its details from the `WebAuthenticationDetailsSource`, and register it in the `SecurityContextHolder`.

#### Scenario: Valid token sets authentication in context
- **WHEN** a request arrives with a valid `Authorization: Bearer <token>`
- **AND** no authentication is already present in the `SecurityContextHolder`
- **THEN** `SecurityContextHolder.getContext().getAuthentication()` is non-null after the filter executes
- **THEN** the authenticated principal's username equals the email encoded in the token

#### Scenario: Already-authenticated request is not re-authenticated
- **WHEN** a request arrives with a valid `Authorization: Bearer <token>`
- **AND** authentication is already present in the `SecurityContextHolder`
- **THEN** the filter does not replace the existing authentication

#### Scenario: Token for non-existent user is rejected
- **WHEN** a request arrives with a token whose subject does not match any user
- **THEN** `UsernameNotFoundException` is caught, no authentication is set, and the request continues to the chain

### Requirement: Invalid or expired tokens are handled without exception propagation
The system SHALL wrap JWT parsing in a try-catch. If `JwtService.extractUsername(token)` or `JwtService.isTokenValid` throws any exception (expired token, malformed token, signature mismatch), the filter SHALL not set authentication in the `SecurityContextHolder` and SHALL allow Spring Security to reject the request at the authorization layer.

#### Scenario: Expired token does not break the request
- **WHEN** a request arrives with an expired `Bearer` token
- **THEN** no exception propagates past the filter
- **THEN** `SecurityContextHolder.getContext().getAuthentication()` is null after the filter
- **THEN** the response status for a protected route is 403 or 401

#### Scenario: Malformed token does not break the request
- **WHEN** a request arrives with `Authorization: Bearer not.a.real.token`
- **THEN** no exception propagates past the filter
- **THEN** `SecurityContextHolder.getContext().getAuthentication()` is null after the filter

### Requirement: Filter is registered before UsernamePasswordAuthenticationFilter
The system SHALL register `JwtAuthenticationFilter` in the `SecurityFilterChain` before `UsernamePasswordAuthenticationFilter` so JWT validation occurs prior to any form-based authentication attempt.

#### Scenario: Filter position in chain
- **WHEN** the Spring Security filter chain is initialized
- **THEN** `JwtAuthenticationFilter` executes before `UsernamePasswordAuthenticationFilter` for every request
