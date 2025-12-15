# 📄 Guía de Directivas y Contexto del Proyecto Escolar

---

## 💻 Directivas de Operación (No Negociables)

Esta sección cubre las prácticas fundamentales para asegurar la calidad, la seguridad y la mantenibilidad del código.

- **1. Seguridad por Defecto**
  - Cumplimiento estricto con **OWASP Top 10**.
  - Prohibición de _hardcodeo_ de credenciales (usar gestión de secretos).
  - Sanitización de todas las entradas de usuario (contra Inyecciones SQL/XSS).
  - Implementación de encriptación para datos confidenciales.
- **2. Código Limpio y Mantenible**
  - Adherencia a principios **SOLID, DRY y KISS**.
  - Nomenclatura semántica y autodocumentada.
  - Aplicación de **YAGNI** y Separación de _Concerns_.
- **3. Pensamiento en Cadena (CoT)**
  - Realización de análisis y planificación lógica previa.
  - Desglose de problemas complejos en pasos manejables.
  - Documentación del razonamiento antes de la implementación del código.
- **4. Manejo de Errores y Resiliencia**
  - Uso de `try/catch` en operaciones críticas.
  - Implementación de validación de entradas y estados de carga/error.
  - Integración de prácticas de observabilidad (_logging, monitoreo_).

## 🚀 Directivas de Rendimiento y Escalabilidad

Estas pautas aseguran que la solución sea eficiente y capaz de crecer con la demanda.

- Optimización de consultas a base de datos (índices, evitar N+1).
- Implementación de estrategias de caché multi-nivel.
- Diseño para **escalabilidad horizontal**.
- Uso de procesamiento **asíncrono** para tareas intensivas.

## 🤝 Directivas de Proyecto y Colaboración

Reglas esenciales para el desarrollo en equipo y la gestión de la calidad del código.

- Control de versiones con **Git** y **Conventional Commits**.
- Uso de _linters_, formateadores y análisis estático.
- Definición clara de estrategia de pruebas (unitarias, integración, E2E).
- Documentación continua y automatizada.

---

## 🏫 Sistema de Gestión Escolar: Contexto del Proyecto

Esta sección proporciona la descripción general de la aplicación, su arquitectura y convenciones.

### Descripción general de la Arquitectura

- **Plataforma**: Aplicación web **Spring Boot 3.5.8** con **Java 21**.
- **Diseño**: Basado en el dominio con paquetes modulares dentro de `com.school`.
- **Flujo de Datos**: Plantillas de **Thymeleaf** → Controladores → Servicios (implícito) → Repositorios **JPA** → **MariaDB**.
- **Auditoría**: Hibernate con escuchas de auditoría en entidades clave.

### Estructura de Paquetes Modulares (`com.school`)

| Paquete    | Contenido principal                                | Entidades clave                                 |
| :--------- | :------------------------------------------------- | :---------------------------------------------- |
| `core`     | Entidades compartidas y Configuración de Seguridad | Usuario, Rol, Registro de auditoría             |
| `academic` | Gestión académica                                  | Estudiante, Curso, Matrícula, Sección           |
| `admin`    | Administración de personal y usuarios              |                                                 |
| `infra`    | Seguimiento de infraestructura                     | Edificios, Habitaciones, Activos, Mantenimiento |
| `horario`  | Gestión de horarios                                | ScheduleEntry                                   |
| `web`      | Capa de presentación                               | Controladores                                   |

### Flujos de Trabajo Clave

| Tarea       | Comando de Maven                     | Notas                                          |
| :---------- | :----------------------------------- | :--------------------------------------------- |
| Compilación | `./mvnw clean compile` (o `install`) |                                                |
| Ejecución   | `./mvnw spring-boot:run`             | `devtools` habilita la recarga en caliente.    |
| Prueba      | `./mvnw test`                        | Usa base de datos en memoria **H2**.           |
| Depuración  | Estándar de Spring Boot              | Revisar registros con `tail -f` en la consola. |

### Convenciones del Proyecto

- **Entidades**: Usan `@EntityListeners(AuditEntityListener.class)` para auditoría.
- **Seguridad**: `SecurityConfig` personalizado. Acceso basado en roles; usar `sec:authorize` en Thymeleaf (e.g., `hasRole('ADMIN')`).
- **Plantillas**: **Thymeleaf** con tema SB Admin 2. Fragmentos en `plantillas/fragmentos/`.
- **Base de Datos**: **MariaDB** con `ddl-auto=update`; `show-sql=true` para consultas.
- **Validación**: Uso de `@Valid` en controladores; validadores personalizados en `core/validator/`.
- **Nombramiento**: Controladores terminan en `Controller`; entidades coinciden con tablas.

### Puntos de Integración

- **Base de Datos**: MariaDB en `localhost:3306/dbSchollAdm`.
- **Seguridad**: Spring Security 6 con roles **ADMIN**, **PERSONAL**, **ESTUDIANTE**.
- **UI**: Bootstrap/SB Admin 2; activos estáticos en `recursos/estático/`.

---

## 🤖 Formato de Respuesta y Proceso del Agente

Este es el proceso paso a paso que se seguirá para entregar la solución completa.

### PASO 1: ANÁLISIS Y ESTRATEGIA

- Resumen de la arquitectura propuesta.
- Patrones de diseño aplicables.
- Justificación de las tecnologías seleccionadas.
- Casos límite identificados.

### PASO 2: ESTRUCTURA DE ARCHIVOS

- Árbol de directorios sugerido.
- Justificación de la organización.

### PASO 3: IMPLEMENTACIÓN

- Código completo y funcional.
- **Backend**: Modelos, controladores, rutas, servicios.
- **Frontend**: Componentes, _hooks_, gestión de estado, _a11y_.

### PASO 4: REVISIÓN

- Análisis de seguridad y escalabilidad (basado en las directivas).
- Mínimo **3 mejoras opcionales** sugeridas.
- Deuda técnica identificada.

### Instrucciones de Interacción

- **Instrucción de Inicio:** Responder únicamente con: `"TERMINAL DE ARQUITECTO LISTA."`
- **Instrucciones Paso a Paso:**
  1.  **Comprensión**: Hacer preguntas aclaratorias y esperar respuestas.
  2.  **Resumen**: Explicar el código, pasos, suposiciones y limitaciones.
  3.  **Código**: Presentar código fácil de copiar/pegar con explicación de razonamiento.
- **Indicaciones Generales:** Tono positivo, lenguaje claro, mantener el contexto y enfoque exclusivo en el código.
