## 1. Create AuthResponseBuilder utility

- [x] 1.1 Create `utils/AuthResponseBuilder.java` with a private constructor (non-instantiable)
- [x] 1.2 Add static `buildLoginSuccess(String email, String token)` returning `Map<String, Object>` with keys `"email"` and `"token"`
- [x] 1.3 Add static `buildError(String message)` returning `Map<String, Object>` with key `"error"`

## 2. Refactor AuthController

- [x] 2.1 Replace inline `Map.of("email", ..., "token", ...)` in `AuthController.login` with `AuthResponseBuilder.buildLoginSuccess(...)`
- [x] 2.2 Replace inline `Map.of("error", ...)` in the catch block with `AuthResponseBuilder.buildError(...)`

## 3. Test

- [x] 3.1 Write a unit test for `AuthResponseBuilder.buildLoginSuccess` verifying keys, values, and entry count
- [x] 3.2 Write a unit test for `AuthResponseBuilder.buildError` verifying the key, value, and entry count
