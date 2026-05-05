## ADDED Requirements

### Requirement: Protected ping endpoint returns authenticated user info
The system SHALL expose `GET /api/test/ping` that requires a valid JWT token. When called with a valid `Authorization: Bearer <token>` header, the endpoint SHALL return HTTP 200 with a JSON body containing the authenticated user's `email` and `roles`.

#### Scenario: Valid token returns user info
- **WHEN** `GET /api/test/ping` is called with a valid `Authorization: Bearer <token>`
- **THEN** the response status is 200
- **THEN** the response body contains an `email` field matching the token subject
- **THEN** the response body contains a `roles` field with the user's authorities

#### Scenario: Missing token returns 401 or 403
- **WHEN** `GET /api/test/ping` is called without an `Authorization` header
- **THEN** the response status is 401 or 403

#### Scenario: Expired token returns 401 or 403
- **WHEN** `GET /api/test/ping` is called with an expired Bearer token
- **THEN** the response status is 401 or 403

### Requirement: Public endpoint is accessible without authentication
The system SHALL expose `GET /api/test/public` that does not require authentication. The endpoint SHALL return HTTP 200 with a fixed message confirming the server is reachable.

#### Scenario: Public endpoint responds without token
- **WHEN** `GET /api/test/public` is called without any `Authorization` header
- **THEN** the response status is 200
- **THEN** the response body contains a non-empty `message` field

#### Scenario: Public endpoint also responds with a valid token
- **WHEN** `GET /api/test/public` is called with a valid Bearer token
- **THEN** the response status is 200

### Requirement: SecurityConfig permits /api/test/public without authentication
The system SHALL add `/api/test/public` to the `requestMatchers(...).permitAll()` rule in `SecurityConfig` so that the JWT filter does not block unauthenticated requests to that path.

#### Scenario: SecurityConfig allows /api/test/public
- **WHEN** the Spring Security filter chain processes a request to `/api/test/public`
- **THEN** the request is not blocked by the authentication requirement
