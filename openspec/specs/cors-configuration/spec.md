## ADDED Requirements

### Requirement: CORS allows configured frontend origin
The system SHALL expose a `CorsConfigurationSource` Spring bean that permits requests from the origin defined by the `CORS_ALLOWED_ORIGIN` environment variable. If the variable is not set, the default value SHALL be `http://localhost:4200`. The configuration SHALL be applied globally to all routes via `httpSecurity.cors()`.

#### Scenario: Request from configured origin is not blocked
- **WHEN** a preflight OPTIONS request arrives from the configured allowed origin
- **THEN** the response includes `Access-Control-Allow-Origin` matching that origin
- **THEN** the response status is 200

#### Scenario: Request from unauthorized origin is blocked
- **WHEN** a preflight OPTIONS request arrives from an origin not in the allowed list
- **THEN** the response does not include a permissive `Access-Control-Allow-Origin`

### Requirement: CORS allows required HTTP methods
The system SHALL permit the following HTTP methods in the CORS configuration: `GET`, `POST`, `PUT`, `DELETE`, `OPTIONS`. No other methods need to be allowed.

#### Scenario: Allowed methods are reflected in preflight response
- **WHEN** a preflight OPTIONS request is sent from an allowed origin with `Access-Control-Request-Method: POST`
- **THEN** the response includes `Access-Control-Allow-Methods` containing at least `GET, POST, PUT, DELETE, OPTIONS`

### Requirement: CORS allows Authorization and Content-Type headers
The system SHALL permit the `Authorization` and `Content-Type` request headers in the CORS configuration so that clients can send JWT tokens and JSON payloads.

#### Scenario: Authorization header is allowed in preflight
- **WHEN** a preflight OPTIONS request is sent with `Access-Control-Request-Headers: Authorization, Content-Type`
- **THEN** the response includes `Access-Control-Allow-Headers` containing `Authorization` and `Content-Type`

### Requirement: CORS configuration is applied to all paths
The system SHALL map the CORS configuration to `/**` so that every endpoint (including `/api/auth/**` and protected routes) is covered.

#### Scenario: CORS applies to auth endpoints
- **WHEN** a preflight OPTIONS request is sent to `/api/auth/login` from an allowed origin
- **THEN** CORS headers are present in the response

#### Scenario: CORS applies to protected endpoints
- **WHEN** a preflight OPTIONS request is sent to `/api/estudiantes` from an allowed origin
- **THEN** CORS headers are present in the response

### Requirement: Session management is STATELESS
The system SHALL configure `SessionCreationPolicy.STATELESS` in the `SecurityFilterChain` so that no HTTP session is created or used for authentication state.

#### Scenario: No session cookie is issued after login
- **WHEN** a successful `POST /api/auth/login` request is processed
- **THEN** the response does not contain a `Set-Cookie` header with a session identifier
