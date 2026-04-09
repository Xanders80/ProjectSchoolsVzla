# Hallucination Guard - Code Verification Prompt

## Verificación de Código para SMS

### Checklist de Verificación

#### Imports
- [ ] Todos los imports existen en el classpath
- [ ] No hay imports de paquetes que no existen
- [ ] Los imports de `com.school.*` apuntan a clases existentes

#### Annotations
- [ ] `@Entity`, `@Table`, `@Column` son de `jakarta.persistence`
- [ ] `@Service`, `@Repository`, `@Controller` son de `org.springframework`
- [ ] `@PreAuthorize` es de `org.springframework.security`
- [ ] `@Valid` es de `jakarta.validation`

#### Métodos de Repository
- [ ] Los métodos `findBy...` siguen la convención de Spring Data
- [ ] Los métodos custom tienen `@Query` con JPQL válido
- [ ] No se inventan métodos que no existen en la interface

#### Thymeleaf
- [ ] Los fragments referenciados existen
- [ ] Las keys de i18n siguen el patrón del proyecto
- [ ] Los bindings de formulario coinciden con los campos de la entidad

### Verificación Automática
```bash
# Compilar para verificar
mvn compile -q

# Verificar imports
mvn compile 2>&1 | grep "cannot find symbol"

# Verificar Thymeleaf
mvn spring-boot:run &
curl -s http://localhost:8080/students | grep -i "error"
```
