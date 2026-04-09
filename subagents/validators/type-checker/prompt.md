# Type Checker Prompt

Verifica los tipos en el código Java del School Management System.

## Checks de Tipos

### 1. Firmas de Métodos
- Tipos de parámetros correctos
- Tipo de retorno consistente
- Overloading correcto

### 2. Generics
- Type parameters válidos
- Wildcards correctos
- Bounded type parameters válidos

### 3. Asignaciones
- Tipo de variable compatible con valor
- Casting seguro
- Autoboxing/unboxing correcto

### 4. Expresiones
- Operadores con tipos compatibles
- Condicionales booleanos
- Strings en concatenación

### 5. JPA Types
- @Column type compatible con Java type
- @Enumerated correcto
- @Temporal correcto
- @Lob/@Blob correcto

## Errores Comunes en SMS
```java
// MAL: Tipo incompatible
Long id = "123"; // String a Long

// MAL: Generic incorrecto
List<String> students = studentRepository.findAll(); // findAll returns List<Student>

// MAL: Enum incorrecto
@Enumerated(EnumType.ORDINAL) // Debería ser STRING
private Role role;

// BIEN
Long id = 123L;
List<Student> students = studentRepository.findAll();
@Enumerated(EnumType.STRING)
private Role role;
```
