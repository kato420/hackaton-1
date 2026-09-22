# Plan de División de Tareas - Equipo G00

Para simular un desarrollo en equipo y tener un historial de commits realista, hemos dividido el código final (que ya está probado al 100% en `main`) en tres ramas principales. Cada integrante debe crear su propia rama a partir de un punto base limpio, añadir su parte del código (pueden copiarlo de la rama `main` o pedirle a la IA que lo regenere) y hacer un commit y push. Luego, hacen Pull Requests hacia `main`.

## 👩‍💻 1. Sandra Carolina
**Rama sugerida:** `feat/sandra-auth-nodos`
**Misión:** Configuración de Seguridad (Estrella 1) y Gestión de Nodos (Estrella 2).

**Pasos a realizar:**
1. Crear el paquete `security` y agregar el soporte para **JWT** (`JwtService`, `JwtAuthenticationFilter`, `SecurityConfig`).
2. Crear la entidad `User` y `StoryNode` con sus repositorios.
3. Crear el `NodeController` asegurando que la creación de nodos sea exclusiva del rol administrador.
4. Crear el `DataInitializer` para que la base de datos arranque con el usuario QA configurado.
**Prompt para la IA (si usas IA en tu turno):** 
> "Configura el JWT en Spring Boot para este proyecto y crea los endpoints de la Estrella 1 y 2. Ya tenemos las entidades User y StoryNode. Usa la configuración final del proyecto."

---

## 👨‍💻 2. Joel Rodrigo
**Rama sugerida:** `feat/joel-partidas`
**Misión:** Motor de Partidas (Estrella 3) y la estructura base de Decisiones.

**Pasos a realizar:**
1. Crear la entidad `Playthrough` (Partida) y su repositorio.
2. Crear el `PlaythroughController` y `PlaythroughService` con las reglas de negocio (lucidez en 100, control en 0, asignación de nodo inicial).
3. Añadir el endpoint del historial de la partida (`GET /api/v1/playthroughs/{id}/path`) con los campos `fromNodeCode` y `toNodeCode`.
4. Crear la estructura inicial de la entidad `Decision` y sus DTOs (`DecisionRequest`, `PaginatedResponse`).
**Prompt para la IA (si usas IA en tu turno):** 
> "Basándote en el código de Nodos, implementa la Estrella 3 (Partidas). Asegúrate de validar que los nodos tengan capacidad y que el historial de decisiones devuelva el formato exacto requerido por el QA."

---

## 👨‍💻 3. Alexis HY
**Rama sugerida:** `feat/alexis-consecuencias-async`
**Misión:** Sistema de Consecuencias (Estrella 4) y Motor Asíncrono de Notificaciones (Estrella 5).

**Pasos a realizar:**
1. Completar el `DecisionService` con las **5 reglas estrictas** de filtrado de texto (normalización, ENTRADA_CORRUPTA, RUPTURA_CUARTA_PARED, etc.).
2. Configurar `@EnableAsync` y crear el `NotificationListener` con eventos de Spring (`@TransactionalEventListener`).
3. Crear la entidad `RealityLog` y su respectivo controlador para auditar los correos.
4. Implementar el envío de correos con `JavaMailSender` manejando la falla simulada (`X-Bandersnatch-Simulate`).
**Prompt para la IA (si usas IA en tu turno):** 
> "Implementa la lógica compleja de la Estrella 4 para el filtrado de texto y consecuencias, y luego agrega la Estrella 5 usando un evento transaccional asíncrono para enviar un correo de Informe de Realidad."

---

## 🚀 Estrategia de Fusión (Merge)
1. **Sandra** debe hacer merge primero, ya que su código (Seguridad y Nodos) es la base de todo.
2. **Joel** hace pull de los cambios de Sandra, resuelve si hay algún conflicto menor (como imports) y hace merge de sus Partidas.
3. **Alexis** hace pull de los cambios de Joel, y finalmente integra el motor de reglas y la asincronía.
