# Lint Enforcer Prompt

Aplica las reglas de linting del School Management System.

## Reglas de Linting

### Java
- Indentación: 4 espacios
- Longitud máxima de línea: 120 caracteres
- No wildcard imports (excepto static)
- Constructor injection obligatorio
- Lombok annotations: @Getter, @Setter, @Builder, @RequiredArgsConstructor, @Slf4j
- Orden de imports: java.*, javax.*, jakarta.*, org.*, com.*, static
- Nombres: PascalCase para clases, camelCase para métodos/variables, UPPER_SNAKE_CASE para constantes

### Thymeleaf
- Todos los textos con #{message.key}
- Formularios con th:action (CSRF automático)
- DataTables para tablas con muchos datos
- Fragments reutilizables

### YAML
- Indentación: 2 espacios
- No tabs
- Keys en snake_case

### SQL
- Keywords en uppercase
- Identificadores en snake_case
- Indentación consistente

## Auto-Fixes
- Ordenar imports automáticamente
- Corregir indentación
- Renombrar variables según convención
- Agregar annotations faltantes
