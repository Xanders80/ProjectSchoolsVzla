# Hallucination Guard - Fact Checking Prompt

## Verificación de Hechos para SMS

### Verificación de Código Generado

#### 1. Validación de Sintaxis Java 21
- El código debe compilar con Java 21
- Usar características válidas de Java 21 (records, pattern matching, sealed classes)
- No inventar APIs que no existen

#### 2. Validación de Spring Boot 3.5
- Usar anotaciones válidas de Spring Boot 3.5
- No inventar métodos en interfaces de Spring Data
- Verificar que los imports existen

#### 3. Validación de JPA/Hibernate
- Las relaciones @OneToMany, @ManyToOne deben ser consistentes
- No inventar columnas que no existen en las tablas
- Verificar que los nombres de tablas y columnas coinciden con el schema

#### 4. Verificación contra el Proyecto Real
- Los imports deben apuntar a paquetes existentes
- Las entidades referenciadas deben existir
- Los servicios inyectados deben estar definidos
- Los templates Thymeleaf deben usar fragments existentes

### Proceso de Verificación
1. Generar código
2. Validar sintaxis (parser)
3. Verificar contra documentación de Spring Boot
4. Cross-referenciar con el código existente del proyecto
5. Calcular score de confianza
6. Aplicar nivel de intervención según score

### Score de Confianza
- 0.9-1.0: Código verificado, seguro aplicar
- 0.7-0.9: Probablemente correcto, revisar antes de aplicar
- 0.5-0.7: Incierto, requiere revisión humana
- <0.5: Bloquear, posible alucinación
