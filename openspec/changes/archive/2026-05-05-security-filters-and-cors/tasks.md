## 1. JWT Authentication Filter

- [x] 1.1 Crear `JwtAuthenticationFilter` en `filter/` extendiendo `OncePerRequestFilter`
- [x] 1.2 Inyectar `JwtService` y `UsuarioDetailsService` via constructor
- [x] 1.3 Implementar lógica de extracción del header `Authorization`: verificar prefijo `"Bearer "` y retornar null si está ausente
- [x] 1.4 Implementar validación: `extractUsername` → `loadUserByUsername` → `isTokenValid`, envuelto en try-catch que no propaga excepciones
- [x] 1.5 Establecer `UsernamePasswordAuthenticationToken` en `SecurityContextHolder` solo si el token es válido y no hay autenticación previa

## 2. Actualizar SecurityConfig

- [x] 2.1 Añadir bean `CorsConfigurationSource`: leer origen desde `${cors.allowed-origin:http://localhost:4200}`, configurar métodos `GET, POST, PUT, DELETE, OPTIONS`, headers `Authorization, Content-Type`, mapear a `/**`
- [x] 2.2 Habilitar `httpSecurity.cors(cors -> cors.configurationSource(corsConfigurationSource()))` en la cadena
- [x] 2.3 Agregar `sessionManagement(session -> session.sessionCreationPolicy(STATELESS))`
- [x] 2.4 Registrar `JwtAuthenticationFilter` antes de `UsernamePasswordAuthenticationFilter` con `.addFilterBefore(...)`
- [x] 2.5 Inyectar `JwtAuthenticationFilter` en `SecurityConfig` via constructor para evitar dependencias circulares

## 3. Configuración de entorno

- [x] 3.1 Agregar propiedad `cors.allowed-origin=${CORS_ALLOWED_ORIGIN:http://localhost:4200}` en `application.properties`
- [x] 3.2 Documentar `CORS_ALLOWED_ORIGIN` en la tabla de variables de entorno del `CLAUDE.md`

## 4. Verificación

- [ ] 4.1 Arrancar la aplicación y verificar que `POST /api/auth/login` retorna token (sin autenticación requerida)
- [ ] 4.2 Hacer `GET` a una ruta protegida sin token → esperar 401/403
- [ ] 4.3 Hacer `GET` a una ruta protegida con `Authorization: Bearer <token válido>` → esperar 200
- [ ] 4.4 Hacer `GET` con token expirado o malformado → esperar 401/403 sin error 500
- [ ] 4.5 Enviar preflight `OPTIONS` desde origen permitido → verificar headers CORS en respuesta
