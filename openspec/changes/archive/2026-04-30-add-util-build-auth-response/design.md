## Context

`AuthController.login` currently constructs both success and error responses inline using raw `Map.of(...)` calls. The `utils/` package was scaffolded for exactly this kind of shared, stateless helper logic. This is a small, self-contained refactor with no behavior change.

## Goals / Non-Goals

**Goals:**
- Centralise auth response construction in a single, testable utility class
- Give the `utils/` package its first concrete resident
- Keep `AuthController` focused on request handling, not response shaping

**Non-Goals:**
- Introducing a typed `LoginResponse` DTO (that belongs in a separate cleanup pass for `dto/`)
- Changing the response keys or HTTP status codes
- Touching any other controller or service

## Decisions

### Static utility class over instance / Spring bean

`AuthResponseBuilder` is pure logic with no state and no dependencies — a `@Component` would add injection overhead for no benefit. Static methods keep callsites clean (`AuthResponseBuilder.buildLoginSuccess(...)`) and are trivially testable without a Spring context.

*Alternative considered*: A record/DTO in `dto/`. Rejected — the proposal explicitly defers the DTO refactor; this change only moves the construction logic, not the return type.

### Returns `Map<String, Object>` not `ResponseEntity`

The utility builds the *body*, not the full response. The controller retains control over status codes and `ResponseEntity` wrapping. This keeps the utility decoupled from HTTP concerns and reusable from multiple endpoints if needed.

## Risks / Trade-offs

- `Map<String, Object>` is still untyped — same trade-off as before, just centralised. Accepted; the DTO pass will address it later.
- Minimal risk: this is purely a structural move with identical observable behaviour.
