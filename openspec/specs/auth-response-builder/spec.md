## Requirements

### Requirement: Build successful auth response
El sistema SHALL proporcionar `AuthResponseBuilder.buildLoginSuccess(UsuarioInfo usuario, String accessToken, String refreshToken, long expiresIn)` que devuelva un `Map<String, Object>` conteniendo exactamente cinco claves: `"usuario"`, `"accessToken"`, `"refreshToken"`, `"tokenType"` y `"expiresIn"`. El valor de `"usuario"` SHALL ser un `Map` con las claves `"id"`, `"email"`, `"rol"`, `"nombre"` y `"apellido"`. El valor de `"tokenType"` SHALL ser siempre la cadena `"Bearer"`, definida como constante interna de la clase. La clave `"email"` a nivel raíz no debe estar presente.

#### Scenario: Mapa raíz contiene exactamente las cinco claves esperadas
- **WHEN** `buildLoginSuccess` es llamado con un `UsuarioInfo` válido, tokens no vacíos y `expiresIn` positivo
- **THEN** el mapa devuelto contiene exactamente 5 entradas
- **THEN** las claves presentes son `"usuario"`, `"accessToken"`, `"refreshToken"`, `"tokenType"` y `"expiresIn"`
- **THEN** la clave `"email"` no está presente en el mapa raíz

#### Scenario: Sub-mapa usuario contiene los cinco campos del UsuarioInfo
- **WHEN** `buildLoginSuccess` es llamado con un `UsuarioInfo` con id=1, email="u@e.com", rol=ESTUDIANTE, nombre="Ana", apellido="López"
- **THEN** el valor de `"usuario"` es un mapa con clave `"id"` igual a 1
- **THEN** el valor de `"usuario"` contiene clave `"email"` igual a `"u@e.com"`
- **THEN** el valor de `"usuario"` contiene clave `"rol"` igual a `ESTUDIANTE`
- **THEN** el valor de `"usuario"` contiene clave `"nombre"` igual a `"Ana"`
- **THEN** el valor de `"usuario"` contiene clave `"apellido"` igual a `"López"`

#### Scenario: tokenType es siempre Bearer
- **WHEN** `buildLoginSuccess` es llamado con cualquier argumento válido
- **THEN** el valor de `"tokenType"` es exactamente `"Bearer"`

#### Scenario: expiresIn refleja el valor recibido
- **WHEN** `buildLoginSuccess` es llamado con `expiresIn = 3600`
- **THEN** el valor de `"expiresIn"` en el mapa es `3600`

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
