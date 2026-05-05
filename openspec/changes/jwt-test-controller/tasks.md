## 1. TestController

- [x] 1.1 Crear `TestController` en `controller/` con `@RestController` y `@RequestMapping("/api/test")`
- [x] 1.2 Implementar `GET /ping`: leer `Authentication` del `SecurityContextHolder`, retornar `Map` con `email` y `roles`
- [x] 1.3 Implementar `GET /public`: retornar `Map` con `message: "Servidor disponible"` sin autenticación

## 2. Actualizar SecurityConfig

- [x] 2.1 Agregar `/api/test/public` al `requestMatchers(...).permitAll()` en `SecurityConfig`

## 3. Verificación

- [x] 3.1 Compilar sin errores (`./mvnw compile`)
- [ ] 3.2 Probar `GET /api/test/public` sin token → esperar 200
- [ ] 3.3 Probar `GET /api/test/ping` sin token → esperar 401/403
- [ ] 3.4 Probar `GET /api/test/ping` con token válido → esperar 200 con email y roles
