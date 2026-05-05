## 1. Implementación de logs en JwtAuthenticationFilter

- [x] 1.1 Agregar anotación `@Slf4j` de Lombok a `JwtAuthenticationFilter`
- [x] 1.2 Agregar log DEBUG cuando la solicitud no tiene header `Authorization` o el valor no empieza con `"Bearer "`
- [x] 1.3 Agregar log INFO cuando la autenticación se establece correctamente en el `SecurityContextHolder`, incluyendo el email del usuario (sin incluir el token)
- [x] 1.4 Separar el `catch` en dos bloques: `UsernameNotFoundException` con WARN "Usuario no encontrado: {email}" y `Exception` genérica con WARN incluyendo el mensaje de la excepción
- [x] 1.5 Verificar que en ningún log aparece el valor raw del token JWT

## 2. Verificación

- [x] 2.1 Compilar el proyecto sin errores (`./mvnw compile`)
- [ ] 2.2 Confirmar que los logs aparecen en el nivel correcto al enviar solicitudes de prueba
