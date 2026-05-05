## Context

`JwtAuthenticationFilter` procesa cada solicitud autenticada pero no emite ningún log. Cuando un token es rechazado, el único síntoma visible es un 403/401 sin contexto. Lombok `@Slf4j` ya está disponible en el proyecto, por lo que no se requiere ninguna dependencia nueva.

## Goals / Non-Goals

**Goals:**
- Agregar logs en los puntos de decisión clave del filtro usando `@Slf4j` de Lombok.
- Usar niveles apropiados: DEBUG para flujo normal, WARN para rechazos esperados.

**Non-Goals:**
- Logging de cuerpos de solicitud o respuesta.
- Métricas o trazabilidad distribuida (Micrometer/OpenTelemetry).
- Cambios en el comportamiento del filtro.

## Decisions

### D1: Niveles de log por evento

| Evento | Nivel | Razón |
|---|---|---|
| Sin header `Authorization` | DEBUG | Ocurre en toda solicitud a rutas públicas; INFO sería muy ruidoso |
| Token extraído | DEBUG | Flujo normal; el email es suficiente como contexto |
| Autenticación establecida | INFO | Evento de negocio relevante para auditoría |
| Token inválido/expirado (catch) | WARN | Problema externo, no error interno |
| Usuario no encontrado | WARN | Igual que el anterior — token bien formado pero sujeto desconocido |

### D2: No loguear el token completo

El token JWT contiene claims que pueden ser sensibles. Solo se loguea el email extraído del subject, nunca el token en sí.

## Risks / Trade-offs

- **[Trade-off] Logs en DEBUG pueden ser verbosos en producción** → Controlable con configuración de nivel de log (`logging.level.com.backend.estudiantes.filter=WARN`).
