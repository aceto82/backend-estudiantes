## Requirements

### Requirement: Generate signed JWT token from UserDetails
The system SHALL provide a `generateToken(UserDetails userDetails)` method in `JwtService` that creates a signed JWT using the configured HMAC secret key. The token SHALL include the subject (email), issued-at timestamp, and expiration timestamp derived from the configured TTL. The system SHALL also provide `generateToken(Map<String, Object> extraClaims, UserDetails userDetails)` to embed additional claims.

#### Scenario: Token generated with valid UserDetails
- **WHEN** `generateToken(userDetails)` is called with a valid `UserDetails` object
- **THEN** a non-null, non-empty JWT string is returned
- **THEN** the token subject equals `userDetails.getUsername()`
- **THEN** `isTokenValid(token, userDetails)` returns `true`

#### Scenario: Token generated with extra claims
- **WHEN** `generateToken(Map.of("rol", "ADMIN"), userDetails)` is called
- **THEN** the returned token contains the `rol` claim with value `"ADMIN"`

#### Scenario: Token expires after configured TTL
- **WHEN** a token is generated and the configured expiration time has elapsed
- **THEN** `isTokenExpired(token)` returns `true`

### Requirement: Login response includes JWT token
The system SHALL return a `token` field in the `POST /api/auth/login` response body upon successful authentication. The token SHALL be a valid, signed JWT generated for the authenticated user.

#### Scenario: Successful login returns token
- **WHEN** `POST /api/auth/login` is called with valid email and password
- **THEN** the response status is 200
- **THEN** the response body contains a `token` field with a non-empty JWT string
- **THEN** the token is valid and the subject equals the user's email

#### Scenario: Failed login does not return token
- **WHEN** `POST /api/auth/login` is called with incorrect credentials
- **THEN** the response status is 400
- **THEN** the response body contains an `error` field and no `token` field

### Requirement: Signed token parsing uses correct JJWT method
The system SHALL use `parseClaimsJws` (not `parseClaimsJwt`) when parsing signed tokens in `JwtService.extractAllClaims`, so that HMAC-signed tokens are accepted without throwing `UnsupportedJwtException`.

#### Scenario: Parsing a generated token succeeds
- **WHEN** `extractAllClaims(token)` is called with a token produced by `generateToken`
- **THEN** a `Claims` object is returned without throwing any exception
- **THEN** `extractUsername(token)` returns the subject used at generation time
