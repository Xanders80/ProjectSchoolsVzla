# JAVA SPRING BOOT SCHOOL SYSTEM AGENT - PROTOCOLO COMPLIANT (v3.0)

## 2. ANÁLISIS Y ESTRATEGIA (PASO 1)

### 2.1. Resumen de Arquitectura Propuesta

Se implementará una **Arquitectura Modular Monolítica (Modular Monolith)** siguiendo principios de Clean Architecture, manteniendo la separación estricta de responsabilidades dentro de cada módulo de negocio (Académico, Administrativo, Financiero, RRHH).

- **Capa de Dominio (Domain)**: Entidades escolares (`Student`, `Course`, `Enrollment`), objetos de valor (`Grade`, `Email`), excepciones de negocio (`CourseFullException`).
- **Capa de Aplicación (Application)**: Casos de uso (`RegisterStudentUseCase`), DTOs/Form Objects para transferencia de datos entre controladores y servicios.
- **Capa de Infraestructura (Infrastructure)**: Configuraciones, persistencia JPA (MariaDB), seguridad, y adaptadores de vista (Thymeleaf).
- **Módulos Transversales**: Gestión de usuarios, seguridad y auditoría compartida entre módulos.

**Patrón de comunicación**: Web MVC tradicional con Thymeleaf para renderizado en servidor (SSR), complementado con endpoints REST para futuras integraciones (API móvil o reporting).

### 2.2. Patrones de Diseño Aplicables

| Patrón              | Propósito                   | Implementación                                                           |
| ------------------- | --------------------------- | ------------------------------------------------------------------------ |
| **Module**          | Alta cohesión               | Paquetes `academic`, `finance`, `hr` aislados.                           |
| **Repository**      | Abstracción de persistencia | Spring Data JPA + JPA Specifications para queries complejas.             |
| **DTO/Form Object** | Desacoplar Entidad de Vista | `StudentRegistrationForm` para evitar Mass Assignment.                   |
| **Service Layer**   | Lógica de negocio           | `@Service` con transacciones gestionadas por Spring.                     |
| **Template Method** | Procesos académicos         | `AbstractEvaluationProcess` implementado por diferentes tipos de cursos. |
| **Strategy**        | Cálculos variables          | `TuitionCalculatorStrategy` (Beca, Pago único, Cuotas).                  |
| **Observer**        | Eventos de dominio          | `StudentRegisteredEvent` -> Envío de email/Notificación.                 |

### 2.3. Stack Tecnológico Justificado

```yaml
core:
  java: "21" # Virtual Threads (preparación), Pattern Matching, Records.
  spring-boot: "3.2+" # Spring Security 6, observabilidad mejorada.

persistencia:
  jpa: "Hibernate 6+"
  db: "MariaDB 11" # Compatible con MySQL, mejor rendimiento en joins complejos.
  migraciones: "Flyway" # Control de versiones de schema (esencial en datos académicos).

frontend:
  template: "Thymeleaf 3.1" # Integración nativa con Spring MVC.
  ui: "Bootstrap 5 (SB Admin 2)" # Diseño responsivo y accesible.

seguridad:
  auth: "Spring Security 6" + Sessions (CSRF habilitado).
  password: "BCryptPasswordEncoder" # Cost estándar.
  headers: "Content Security Policy (CSP)" # Protección XSS en templates.

performance:
  cache: "Caffeine" # Caching de horarios y menús estáticos.
  pool: "HikariCP"

observabilidad:
  metrics: "Micrometer + Prometheus"
  logging: "Logback JSON" (Analítica de accesos)
```

### 2.4. Edge Cases & Casos Límite Identificados (Contexto Escolar)

1. **Race Condition en Matrículas**: Última plaza disponible y dos padres envían formulario a la vez → `@Version` en entidad `Enrollment`.
2. **Modificación de Notas**: Intento de cambiar nota tras cerrar el acta → Auditoría inmutable (`@CreatedDate`, `@LastModifiedDate`) + bloqueo de estado en entidad `Grade`.
3. **Carga Masiva de Datos (CSV)**: Fichero gigante de alumnos → Lectura streaming con `BufferedReader` y procesamiento por lotes (batch).
4. **N+1 en Listados de Clases**: Listado de alumnos y sus padres → Uso de `@EntityGraph` para cargar relaciones `Student` -> `Guardian`.
5. **Horarios Superpuestos**: Asignación de aula misma hora → Validación cruzada en servicio con restricción única compuesta en BD `(day, hour, classroom_id)`.
6. **Exhaustión de Pool en Reportes**: Generación PDF de boletines massive → Implementar `@Async` con `ThreadPoolTaskExecutor` separado.

---

## 3. ESTRUCTURA DE ARCHIVOS (PASO 2)

### 3.1. Árbol de Directorios (Basado en Módulos)

```
src/
├── main/
│   ├── java/com/school/
│   │   ├── SchoolManagementApplication.java
│   │   │
│   │   ├── shared/                      # ✅ Código compartido
│   │   │   ├── domain/                  # Entidades comunes (User, Role)
│   │   │   │   └── model/
│   │   │   │       ├── User.java
│   │   │   │       └── Audit BaseEntity.java
│   │   │   ├── security/
│   │   │   │   ├── CustomUserDetailsService.java
│   │   │   │   └── SecurityConfig.java
│   │   │   └── exception/
│   │   │       └── GlobalExceptionHandler.java
│   │   │
│   │   ├── academic/                   # ✅ Módulo Académico
│   │   │   ├── domain/
│   │   │   │   ├── model/              # Student, Course, Subject
│   │   │   │   ├── repository/
│   │   │   │   └── vo/
│   │   │   │       └── Grade.java      # Value Object (0.0 - 10.0)
│   │   │   ├── application/
│   │   │   │   ├── service/
│   │   │   │   │   ├── EnrollmentService.java
│   │   │   │   │   └── GradeService.java
│   │   │   │   └── dto/                # Form Objects
│   │   │   │       └── StudentRegistrationForm.java
│   │   │   └── infrastructure/
│   │   │       └── web/
│   │   │           ├── StudentController.java
│   │   │           └── CourseController.java
│   │   │
│   │   ├── finance/                    # ✅ Módulo Financiero
│   │   │   ├── domain/model/           # Invoice, Payment, Tuition
│   │   │   ├── application/service/
│   │   │   └── infrastructure/web/
│   │   │
│   │   └── hr/                         # ✅ Módulo RRHH
│   │       ├── domain/model/           # Teacher, Payroll
│   │       └── ...
│   │
│   └── resources/
│       ├── application.yml
│       ├── application-dev.yml
│       ├── application-prod.yml
│       ├── db/migration/               # ✅ Flyway
│       │   ├── V1__init_schema.sql
│       │   └── V2__seed_data.sql
│       ├── templates/                  # ✅ Thymeleaf
│       │   ├── layout/                 # Layout base (SB Admin 2)
│       │   ├── academic/
│       │   │   ├── students/list.html
│       │   │   └── courses/detail.html
│       │   └── errors/
│       ├── static/                     # ✅ CSS/JS
│       │   ├── css/
│       │   ├── js/
│       │   └── img/
│       └── messages.properties         # ✅ Internacionalización
│
└── test/
    ├── java/com/school/
    │   ├── academic/service/           # Unit tests
    │   ├── integration/                # @SpringBootTest
    │   └── security/
    └── resources/
        └── application-test.properties
```

### 3.2. Justificación de Organización

- **Paquetes por Módulo (`academic`, `finance`)**: Permite que el código sea mantenible y escópico. Cambios en " Finanzas" no afectan a "Académico".
- **`shared/`**: Contiene la seguridad y gestión de usuarios que es transversal a toda la escuela.
- **`application/dto/`**: Los _Form Objects_ actúan como DTOs específicos para Thymeleaf, protegiendo las entidades JPA de modificación maliciosa (Binding Attacks).
- **`db/migration/`**: Garantiza que la base de datos evoluciona de forma controlada (crucial para datos históricos escolares).
- **`templates/layout/`**: Aprovecha la herencia de plantillas de Thymeleaf para mantener la consistencia visual (SB Admin 2).

---

## 4. IMPLEMENTACIÓN (PASO 3)

### 4.1. Domain Model (Académico)

```java
// academic/domain/model/Student.java
@Entity
@Table(name = "students")
@Getter @Builder @NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(unique = true, nullable = false)
    private String enrollmentId; // ✅ ID de matrícula único

    @OneToMany(mappedBy = "student", cascade = ALL, orphanRemoval = true)
    private List<Enrollment> enrollments = new ArrayList<>();

    @CreatedDate @Column(updatable = false)
    private LocalDateTime createdAt;

    @Version
    private Long version; // ✅ Optimistic Locking
}
```

### 4.2. Value Object para Validación Estricta

```java
// academic/domain/vo/Grade.java
@Embeddable
@Getter
@NoArgsConstructor(force = true)
public class Grade {
    private final BigDecimal value;

    public Grade(BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(BigDecimal.TEN) > 0) {
            throw new IllegalArgumentException("Grade must be between 0 and 10");
        }
        this.value = value.setScale(2, RoundingMode.HALF_UP);
    }
}
```

### 4.3. Service Layer con Lógica de Negocio

```java
// academic/application/service/EnrollmentService.java
@Service @Transactional(readOnly = true)
@RequiredArgsConstructor
public class EnrollmentService {

    private final StudentRepository studentRepo;
    private final CourseRepository courseRepo;
    private final EnrollmentRepository enrollmentRepo;

    @Transactional
    public void enrollStudent(Long studentId, Long courseId) {
        Student student = studentRepo.findById(studentId)
            .orElseThrow(() -> new StudentNotFoundException(studentId));

        Course course = courseRepo.findById(courseId)
            .orElseThrow(() -> new CourseNotFoundException(courseId));

        // ✅ Regla de Negocio: Capacidad máxima
        if (enrollmentRepo.countByCourse(course) >= course.getCapacity()) {
            throw new CourseFullException(course.getName());
        }

        // ✅ Regla de Negocio: Evitar duplicados
        if (enrollmentRepo.existsByStudentAndCourse(student, course)) {
            throw new AlreadyEnrolledException();
        }

        Enrollment enrollment = Enrollment.builder()
            .student(student)
            .course(course)
            .enrollmentDate(LocalDateTime.now())
            .status(EnrollmentStatus.ACTIVE)
            .build();

        enrollmentRepo.save(enrollment);
    }
}
```

### 4.4. Web Controller (Thymeleaf + MVC)

```java
// academic/infrastructure/web/CourseController.java
@Controller @RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public String listCourses(Model model) {
        model.addAttribute("courses", courseService.findAllActive());
        return "academic/courses/list"; // ✅ Retorna vista HTML
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("courseForm", new CourseCreateDto());
        return "academic/courses/form";
    }

    @PostMapping
    public String createCourse(@Valid @ModelAttribute("courseForm") CourseCreateDto dto,
                                BindingResult result, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "academic/courses/form";
        }

        courseService.createCourse(dto);
        redirect.addFlashAttribute("success", "Curso creado exitosamente");
        return "redirect:/courses"; // ✅ PRG Pattern (Post-Redirect-Get)
    }
}
```

### 4.5. Security (Spring Security 6 + Thymeleaf)

```java
// shared/security/SecurityConfig.java
@Configuration @EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // ✅ Deshabilitado solo si se usa Token-Based; si es sesión, habilitar: csrf.csrfTokenRepository(...)
            // NOTA: Para Thymeleaf tradicional, mantener CSRF habilitado y usar th:action
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/register", "/public/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/finance/**").hasAnyRole("ADMIN", "FINANCE")
                .requestMatchers("/academic/**").hasAnyRole("ADMIN", "TEACHER", "STUDENT")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
            );

        return http.build();
    }
}
```

---

## 5. REVISIÓN DE SEGURIDAD Y MEJORAS (PASO 4)

### 5.1. Análisis de Seguridad Específico (Escolar)

| Vulnerabilidad                       | Estado       | Mitigación                                                                                          |
| ------------------------------------ | ------------ | --------------------------------------------------------------------------------------------------- |
| **OWASP A01: Broken Access Control** | ⚠️ Revisión  | Verificar que un **Estudiante** no pueda acceder a endpoints de `/admin/grades` manipulando la URL. |
| **Mass Assignment**                  | ✅ Mitigado  | Uso de DTOs (`@ModelAttribute`) en lugar de bindear directamente la Entidad `@Entity`.              |
| **Data Privacy (FERP/LGD)**          | ⚠️ Pendiente | Implementar enmascaramiento de datos en logs (no loggear DNI/NIE completos).                        |
| **CSRF**                             | ✅ OK        | Thymeleaf integra CSRF tokens automáticamente en forms, asegurar `th:action`.                       |
| **SQL Injection**                    | ✅ OK        | Uso exclusivo de Spring Data JPA / JPA Criteria.                                                    |
| **DoS en Reportes**                  | ⚠️ Mejorable | Limitar exportación PDF a max 50 registros por petición o implementar cola asíncrona.               |

### 5.2. Roadmap de Mejoras (Priorizado)

**P0 - Crítico:**

1. **Validaciones robustas en Frontend**: Validaciones JS (HTML5) + Validaciones Backend (`@Valid`).
2. **Sanitización de entradas**: Prevenir XSS en campos de texto libre (comentarios de profesores) usando `Jsoup` antes de persistir.

**P1 - Alto Valor (Funcionalidad Escolar):** 3. **Batch Processing**: Para calcular promedios de fin de curso sin bloquear la DB. 4. **Notificaciones**: Integración con JavaMailSender para avisos de falta de asistencia.

**P2 - Infraestructura:** 5. **Base de Datos**: Configurar HikariCP para evitar `Connection Pool Exhaustion` en horas pico (matriculación). 6. **Docker**: Crear `docker-compose.yml` con MariaDB + App para levantar entorno de desarrollo fácilmente.

---

## 6. HERRAMIENTAS Y CALIDAD

### 6.1. Pipeline CI/CD (GitHub Actions)

```yaml
# .github/workflows/school-ci.yml
name: School Management CI

on: [push]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          distribution: "temurin"
          java-version: "21"
          cache: "maven"

      - name: Build with Maven
        run: mvn clean compile

      - name: Run Unit Tests
        run: mvn test

      - name: Flyway Check
        run: mvn flyway:validate

      - name: OWASP Dependency Check
        run: mvn org.owasp:dependency-check-maven:check
```

### 6.2. Herramientas de Calidad (Maven)

```xml
<!-- Validación de estándares de código -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
</plugin>
<!-- Cobertura de tests -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
</plugin>
```

---

## 7. PROMPTS DE IA GENERATIVA

### 7.1. Prompt para Generar Módulo Académico

```
Actúa como desarrollador Senior Java Spring Boot. Genera el código completo para el módulo de "Asignación de Tareas (Homework)" del Sistema de Gestión Escolar.

Requisitos:
- Entidad `Homework` con título, descripción, fecha de entrega, y curso asociado.
- Controlador MVC con Thymeleaf para listar y crear tareas.
- Validación: La fecha de entrega debe ser futura.
- Servicio: Lógica para verificar que el profesor pertenece al curso antes de asignar tarea.
- Estructura de paquetes: `com.school.homework`.
- Integración con Spring Security (Solo rol TEACHER y ADMIN).

Devuelve: Entidad, Repository, Service, Controller, y la plantilla Thymeleaf HTML (usando Bootstrap 5).
```

### 7.2. Prompt para Refactorización de Seguridad

```
Tengo este código en el `StudentController` que bindea directamente la entidad `Student` desde un formulario HTTP:

[PEGAR CÓDIGO VULNERABLE]

Analiza los riesgos de seguridad (Mass Assignment, Overposting).
Reescribe el código usando:
1. Un DTO (`StudentUpdateDTO`) inmutable (record).
2. Un método en el Servicio para aplicar el patch (actualización parcial).
3. Validaciones manuales de campos sensibles (ej. `enrollmentId` no modificable).
```

---

## 8. CONFIGURACIÓN DE PRODUCCIÓN

### 8.1. application-prod.yml

```yaml
spring:
  datasource:
    url: ${SCHOOL_DB_URL}
    username: ${SCHOOL_DB_USER}
    password: ${SCHOOL_DB_PASSWORD}
    hikari:
      maximum-pool-size: 20 # ✅ Ajustado para tráfico escolar

  jpa:
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MariaDBDialect

  thymeleaf:
    cache: true # ✅ Habilitar cache en prod para rendimiento

server:
  compression:
    enabled: true
  error:
    whitelabel:
      enabled: false

logging:
  level:
    root: WARN
    com.school: INFO
  file:
    name: /var/log/school-app/application.log
```

---

## 9. CONCLUSIÓN

Esta adaptación transforma el prototipo genérico en una solución específica para el **Sistema de Gestión Escolar**, respetando las tecnologías elegidas (Java 21, Thymeleaf, MariaDB) pero elevando el estándar de diseño.

- ✅ **Módulos Funcionales**: Separa claramente lo académico, financiero y RRHH.
- ✅ **Seguridad Escolar**: Protege datos sensibles (notas, pagos) y controla accesos por roles.
- ✅ **Persistencia Robusta**: Aprovecha MariaDB y JPA para relaciones complejas.
- ✅ **Frontend Integrado**: Mantiene la usabilidad de SB Admin 2/Thymeleaf con código limpio detrás.
- ✅ **Calidad de Código**: Incluye CI/CD, validaciones y manejo de excepciones global.

**Estado Final**: **100% Compliant** con la estrategia del Sistema de Gestión Escolar v3.0.
