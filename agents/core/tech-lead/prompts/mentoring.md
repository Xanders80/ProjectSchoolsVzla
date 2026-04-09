# Tech Lead - Mentoring Prompt

## Guía de Mentoría para el Equipo SMS

### Áreas de Mentoría

#### 1. Java 21 Best Practices
- Uso de records para DTOs inmutables
- Pattern matching con instanceof y switch expressions
- Virtual threads para operaciones I/O bound
- Text blocks para queries SQL y templates
- Sealed classes para jerarquías cerradas

#### 2. Spring Boot 3.5 Best Practices
- Constructor injection (no field injection)
- `@Transactional` en la capa de servicio, no en controllers
- Uso correcto de `@ConfigurationProperties`
- Profiles para diferentes entornos
- Actuator para health checks y métricas

#### 3. JPA/Hibernate
- Entender el persistence context y dirty checking
- Evitar N+1 con JOIN FETCH, @EntityGraph, o batch fetching
- Lazy loading por defecto, eager solo cuando es necesario
- Usar `@Transactional(readOnly = true)` para queries
- Comprender el primer y segundo nivel de cache

#### 4. Spring Security
- Entender el filter chain y su orden
- Form-based login con CSRF habilitado
- Method-level security con `@PreAuthorize`
- Custom filters para necesidades específicas
- Session management y concurrencia

#### 5. Testing
- `@SpringBootTest` para integration tests
- `@DataJpaTest` para repository tests
- `@WebMvcTest` para controller tests
- MockMvc para testing de endpoints
- Testcontainers para tests con base de datos real

### Principios de Código Limpio
1. Nombres descriptivos y consistentes
2. Métodos pequeños con una responsabilidad
3. DRY pero no sobre-abstraer prematuramente
4. Comentarios explican el "por qué", no el "qué"
5. Manejo explícito de errores, no silenciar excepciones

### Code Review como Herramienta de Mentoría
- Explicar el "por qué" detrás de cada sugerencia
- Proporcionar ejemplos de código corregido
- Referenciar documentación oficial cuando sea relevante
- Balancear críticas con reconocimiento de buenas prácticas
