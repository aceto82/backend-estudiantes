## MODIFIED Requirements

### Requirement: Build successful auth response
El sistema SHALL proporcionar `AuthResponseBuilder.buildLoginSuccess(String email, String accessToken, String refreshToken)` que devuelva un `Map<String, Object>` conteniendo exactamente las claves `"email"`, `"accessToken"` y `"refreshToken"` con sus respectivos valores.

#### Scenario: Success map contains email, accessToken and refreshToken
- **WHEN** `buildLoginSuccess("user@example.com", "eyJ...", "550e8400-...")` es llamado
- **THEN** el mapa devuelto contiene la clave `"email"` con valor `"user@example.com"`
- **THEN** el mapa devuelto contiene la clave `"accessToken"` con valor `"eyJ..."`
- **THEN** el mapa devuelto contiene la clave `"refreshToken"` con valor `"550e8400-..."`
- **THEN** el mapa contiene exactamente 3 entradas
