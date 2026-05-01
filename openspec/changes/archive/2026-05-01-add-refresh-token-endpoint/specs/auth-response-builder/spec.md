## ADDED Requirements

### Requirement: Build refresh success response
El sistema SHALL proporcionar `AuthResponseBuilder.buildRefreshSuccess(String accessToken, String refreshToken, long expiresIn)` que devuelva un `Map<String, Object>` con exactamente cuatro claves: `"accessToken"`, `"refreshToken"`, `"tokenType"` (valor `"Bearer"` de la constante existente) y `"expiresIn"`. El mapa NO SHALL contener la clave `"usuario"`.

#### Scenario: Mapa contiene exactamente las cuatro claves esperadas
- **WHEN** `buildRefreshSuccess("eyJ...", "uuid-...", 3600L)` es llamado
- **THEN** el mapa devuelto contiene exactamente 4 entradas
- **THEN** las claves presentes son `"accessToken"`, `"refreshToken"`, `"tokenType"` y `"expiresIn"`
- **THEN** la clave `"usuario"` no está presente

#### Scenario: tokenType es Bearer y expiresIn refleja el valor recibido
- **WHEN** `buildRefreshSuccess` es llamado con `expiresIn = 3600`
- **THEN** el valor de `"tokenType"` es `"Bearer"`
- **THEN** el valor de `"expiresIn"` es `3600`
