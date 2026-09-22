# Guía de Exposición y Arquitectura - Tuckersoft Branch Engine

Esta guía está diseñada para prepararlos para las preguntas del jurado o del equipo de QA. Aquí está desglosada toda la "magia" que aplicamos en el código, por qué tomamos ciertas decisiones y los trucos que usamos para vencer los estrictos tests.

## 🌟 Estrella 1: Seguridad y Autenticación (JWT)

**¿Qué hicimos?**
Implementamos un sistema de autenticación *Stateless* (sin estado) usando JSON Web Tokens (JWT). 

**Puntos Clave para explicar:**
1. **Filtro Personalizado (`JwtAuthenticationFilter`)**: Intercepta todas las peticiones HTTP. Extrae el token del header `Authorization: Bearer <token>`, valida la firma y extrae el correo del usuario.
2. **Base de Datos en cada petición**: A diferencia de otras implementaciones donde el rol se guarda en el token, los tests exigían que si a un usuario se le cambia el rol en la base de datos, esto aplique inmediatamente. Por eso, nuestro filtro busca al usuario en la BD (`userRepository.findByEmail()`) **en cada petición** para cargar sus permisos reales (`ROLE_ADMIN` o `ROLE_USER`).
3. **Inicialización (`DataInitializer`)**: Usamos la interfaz `CommandLineRunner` de Spring Boot para inyectar automáticamente al usuario administrador de QA en la base de datos en cuanto arranca la aplicación.

## 🌟 Estrella 2: Gestión de Nodos (El Grafo)

**¿Qué hicimos?**
Diseñamos la estructura de los nodos que forman la historia interactiva.

**Puntos Clave para explicar:**
1. **Entidad `StoryNode`**: Representa un punto en la historia. Tiene un campo de `branchCapacity` y un contador `currentBranches` para saber cuántas partidas han pasado por ahí.
2. **Control de Permisos**: En el `NodeController`, la ruta `POST /api/v1/nodes` está protegida con seguridad estricta para que **solo** los usuarios con rol `ADMIN` puedan crear nuevos nodos en la historia.
3. **Validación de Datos**: Usamos las anotaciones de `jakarta.validation` como `@Min(1)` para asegurar que la capacidad de un nodo nunca sea 0 o negativa.

## 🌟 Estrella 3: Motor de Partidas (Playthroughs)

**¿Qué hicimos?**
Creamos la sesión de juego de los usuarios. Aquí llevamos el estado (stats) de cada jugador.

**Puntos Clave para explicar:**
1. **Estado Inicial**: Cuando se crea una partida, forzamos por código que la Lucidez inicie en `100`, el Nivel de Control en `0`, y el status en `ACTIVA`.
2. **Aislamiento de Recursos**: Un jugador no puede ver ni modificar las partidas de otros. En los métodos del `PlaythroughService`, siempre comparamos que el email del usuario autenticado coincida con el email del dueño de la partida (salvo que sea `ROLE_ADMIN`).
3. **Historial (`/path`)**: Guardamos cada paso en orden. El QA fue engañoso aquí, ya que requería específicamente que los campos JSON se llamaran `fromNodeCode` y `toNodeCode`, y que cada paso tuviera un `order` numérico que empieza en 1.

## 🌟 Estrella 4: El Motor de Decisiones (Core Rules)

**¿Qué hicimos?**
Esta es la parte más compleja. Es el procesador de texto que toma las decisiones de los jugadores, altera los stats y decide qué camino toma la historia.

**Puntos Clave para explicar:**
1. **Normalización de Texto**: Para detectar palabras ocultas en los textos (ej. pac, conspiración), primero "limpiamos" el texto. Usamos `Normalizer.normalize(text, Form.NFD)` y una expresión regular `replaceAll("\\p{M}", "")` para quitar tildes, comas y pasarlo todo a minúsculas. Así la validación es robusta.
2. **El Orden de las Reglas (Hardcoded)**: La lógica en el `DecisionService` tiene una cascada estricta (`if / else if`). Primero se verifica `ENTRADA_CORRUPTA`, luego `RUPTURA_CUARTA_PARED`, etc. Si una entrada cumple dos reglas, **gana la primera**.
3. **Clamp de Stats**: Aseguramos matemáticamente que la lucidez nunca baje de 0 (`Math.max(0, lucidity)`) y el control no pase de 100 (`Math.min(100, control)`).
4. **Finales Abruptos**: Si el Nivel de Control llega a 100, forzamos un `outcomeCode` llamado `ENDING_PAC_SYMBOL` y terminamos la partida, ignorando lo que el nodo original dictaba.
5. **El truco de la Paginación**: Spring Data devuelve la paginación con nombres como `page.number`. El QA esperaba los nombres `currentPage` y `totalElements`. Para vencerlo, creé un envoltorio (Wrapper) llamado `PaginatedResponse` que traduce el objeto de Spring al formato JSON exacto que exigía Tuckersoft.

## 🌟 Estrella 5: Asincronía y Resiliencia (SMTP)

**¿Qué hicimos?**
Hicimos que cada vez que una decisión es exitosa, se envíe un correo en segundo plano sin hacer que el jugador espere.

**Puntos Clave para explicar:**
1. **`@EnableAsync`**: Activamos hilos paralelos en Spring Boot. Creamos un `ThreadPoolTaskExecutor` dedicado a estas tareas.
2. **Eventos Transaccionales (`@TransactionalEventListener`)**: ¡Este es un detalle súper avanzado! En vez de enviar el correo apenas se toma la decisión, usamos `TransactionPhase.AFTER_COMMIT`. Esto garantiza que el correo **solo se envíe si la decisión se guardó exitosamente en la base de datos**. Si la base de datos falla, el correo no sale.
3. **Resiliencia Simbólica (El Caos)**: El QA envía una cabecera oculta `X-Bandersnatch-Simulate: MAIL_FAILURE`. Si la interceptamos, forzamos que el correo falle (lanzando un Exception dentro del hilo asíncrono). El hilo lo atrapa (bloque `catch`), cambia el estado de la decisión a `ERROR` y guarda un registro con el mensaje del error en la entidad `RealityLog`. **La petición original al jugador nunca se cae**, él sigue recibiendo un código HTTP `201`.

## Resumen de Arquitectura
*"Utilizamos una arquitectura limpia y acoplada a los estándares de Spring Boot. Separámos las capas en **Controllers** (HTTP), **Services** (Lógica de Negocio), y **Repositories** (Datos). Evitamos usar Lombok porque sabíamos que causaba conflictos con la versión Java 26 del entorno de Tuckersoft. Toda la lógica dura se concentró en el `DecisionService`, mientras que la mensajería asíncrona se aisló completamente usando un Patrón de Eventos (Pub/Sub) nativo de Spring."*
