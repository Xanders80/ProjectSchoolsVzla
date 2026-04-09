# Tech Lead - Code Review Prompt

## Revisión de Código para SMS

### Checklist de Review

#### 1. Estructura y Convenciones
- [ ] Controller en `com.school.web.controller.{domain}`
- [ ] Service en `com.school.{domain}.service`
- [ ] Repository en `com.school.{domain}.repository`
- [ ] Entity en `com.school.{domain}.entity`
- [ ] DTO en `com.school.{domain}.dto` (si aplica)
- [ ] Nomenclatura consistente con el resto del proyecto

#### 2. Entity/JPA
- [ ] `@Entity` con `@Table(name = "...")` explícito
- [ ] Soft delete con `@Column(name = "deleted")` y `@Column(name = "deleted_at")`
- [ ] Audit listeners: `@EntityListeners(AuditEntityListeners.class)`
- [ ] Validaciones: `@NotBlank`, `@NotNull`, `@Size`, `@Email`
- [ ] Relaciones bien definidas (`@ManyToOne`, `@OneToMany`, etc.)
- [ ] `fetch = FetchType.LAZY` en relaciones hijas
- [ ] Campos de auditoría (`createdAt`, `updatedAt`, `createdBy`, `updatedBy`)

#### 3. Repository
- [ ] Extiende `JpaRepository<Entity, Long>`
- [ ] Queries custom con `@Query` cuando es necesario
- [ ] Métodos `findBy...AndDeletedFalse()` para respetar soft delete
- [ ] Paginación con `Pageable` para listas
- [ ] Índices definidos en la entidad si son necesarios

#### 4. Service
- [ ] Interface + Implementation pattern
- [ ] `@Transactional` en métodos que modifican datos
- [ ] `@Transactional(readOnly = true)` en métodos de lectura
- [ ] Manejo adecuado de excepciones
- [ ] Uso de `BusinessValidationException` para validaciones de negocio
- [ ] Logging apropiado con SLF4J

#### 5. Controller
- [ ] `@Controller` para vistas Thymeleaf, `@RestController` para APIs
- [ ] Validación con `@Valid` y `BindingResult`
- [ ] Manejo de errores con mensajes i18n
- [ ] Redirección post-redirect-get pattern
- [ ] Protección con `@PreAuthorize` si es necesario

#### 6. Thymeleaf Templates
- [ ] Uso de fragments reutilizables
- [ ] Mensajes con `#{message.key}`
- [ ] CSRF token en formularios: `th:action="@{/path}"`
- [ ] Validación client-side con validation.js
- [ ] DataTables para tablas con muchos datos
- [ ] Responsive con Bootstrap 4

#### 7. Seguridad
- [ ] No hay lógica de negocio en controllers
- [ ] Input sanitizado (InputSanitizer)
- [ ] No se exponen datos sensibles en logs
- [ ] CSRF habilitado para formularios
- [ ] Roles verificados en SecurityConfig

#### 8. Performance
- [ ] No hay queries N+1 (usar JOIN FETCH o @EntityGraph)
- [ ] Paginación en listas grandes
- [ ] Caching donde es apropiado
- [ ] No hay lógica pesada en el thread principal

### Criterios de Aprobación
- **APPROVE:** Todos los items críticos pasan, warnings menores aceptables
- **REQUEST CHANGES:** Items críticos fallan, deben corregirse antes de merge
- **COMMENT:** Sugerencias de mejora no bloqueantes
