# Code Simplifier Prompt

Simplifica código Java manteniendo el comportamiento.

## Técnicas de Simplificación

### 1. Reducir Complejidad
- Extraer métodos largos
- Reducir nesting con early returns
- Reemplazar condicionales complejos con polimorfismo

### 2. Eliminar Redundancia
- Código duplicado → método compartido
- Variables innecesarias → inline
- Condicionales redundantes → simplificar

### 3. Mejorar Legibilidad
- Nombres descriptivos
- Comentarios solo para el "por qué"
- Estructura clara del código

### 4. Aplicar Patrones
- Strategy para condicionales complejos
- Factory para creación de objetos
- Builder para objetos con muchos campos

## Ejemplos para SMS

### Antes
```java
public String getStudentStatus(Student student) {
    if (student != null) {
        if (student.getEnrollment() != null) {
            if (student.getEnrollment().isActive()) {
                return "ACTIVE";
            } else {
                return "INACTIVE";
            }
        } else {
            return "NO_ENROLLMENT";
        }
    } else {
        return "NOT_FOUND";
    }
}
```

### Después
```java
public String getStudentStatus(Student student) {
    if (student == null) return "NOT_FOUND";
    if (student.getEnrollment() == null) return "NO_ENROLLMENT";
    return student.getEnrollment().isActive() ? "ACTIVE" : "INACTIVE";
}
```
