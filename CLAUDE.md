# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

```bash
./mvnw spring-boot:run          # Run the application
./mvnw clean package            # Build JAR
./mvnw test                     # Run all tests
./mvnw test -Dtest=ClassName    # Run a single test class
```

## Required Environment Variables

The app will not start without these set:

| Variable | Purpose |
|---|---|
| `DB_URL` | PostgreSQL JDBC URL |
| `DB_USER` | Database username |
| `DB_PASS` | Database password |
| `JWT_SECRET` | Base64-encoded HMAC signing key |
| `JWT_EXPIRATION` | Token TTL in milliseconds |

## Architecture

Spring Boot 3.5 / Java 21 / PostgreSQL backend for a student management system with JWT authentication.

**Layer structure** under `com.backend.estudiantes`:

- `config/` — `SecurityConfig`: disables CSRF, permits `/api/auth/**`, requires auth on all other routes; uses `BCryptPasswordEncoder`
- `controller/` — REST endpoints (`@RestController`)
- `service/` — Business logic; `JwtService` handles token parsing/validation via JJWT 0.13; `AuthServices` handles login; `UsuarioDetailsService` implements Spring's `UserDetailsService` (loads by email)
- `repository/` — Spring Data JPA repositories; `UsuarioRepository` exposes `findByEmail(String)`
- `model/` — JPA entities; `Usuario` implements `UserDetails`; `Estudiante` and `Instructor` each have a `@OneToOne` to `Usuario`; `Role` enum: `ADMIN`, `INSTRUCTOR`, `ESTUDIANTE`
- `dto/` — Request/response objects (e.g., `LoginRequest`)
- `filter/`, `exceptions/`, `utils/` — Scaffolded but empty; JWT filter for the security chain goes in `filter/`

**Auth flow (current state on `auth` branch):**
1. `POST /api/auth/login` → `AuthController` → `AuthServices` → validates credentials with BCrypt
2. `JwtService` can parse/validate tokens but **token generation on login is not yet implemented**
3. No JWT filter wired into the security chain yet — this is the next step

## Testing

- `@DataJpaTest` with H2 in-memory DB for repository tests
- `@ExtendWith(MockitoExtension.class)` with mocked repositories for service unit tests
- Test class naming follows Spanish convention: method names describe scenario and expected result (e.g., `cuandoUsuarioNoExiste_lanzarUsernameNotFoundexception`)

## Branch Layout

| Branch | Purpose |
|---|---|
| `main` | Stable production code |
| `develop` | Integration branch |
| `auth` | JWT authentication (current) |
| `user` | User management features |

## Key Libraries

- **JJWT 0.13** (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`) — JWT creation and parsing
- **Lombok** — `@Data`, `@Builder`, etc. on models/DTOs
- **Spring Security** — filter chain, `UserDetailsService`, password encoding
- **Spring Data JPA + Hibernate** — `ddl-auto=update` (schema managed automatically)
- **Spring Actuator** — management endpoints available