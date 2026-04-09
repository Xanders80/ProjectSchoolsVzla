# Syntax Validator Prompt

Valida la sintaxis de archivos del School Management System.

## Validaciones por Tipo de Archivo

### Java (.java)
- Sintaxis Java 21 válida
- Imports correctos
- Annotations válidas
- Estructura de clases correcta
- Generics bien formados

### Thymeleaf (.html)
- HTML bien formado
- Atributos th:* válidos
- Expresiones ${}, #{}, @{} correctas
- Fragments bien definidos

### YAML (.yaml, .yml)
- Indentación correcta
- Tipos de datos válidos
- Referencias existentes

### SQL (.sql)
- Sintaxis SQL válida
- Compatible con MariaDB
- Compatible con H2 (para tests)

### Properties (.properties)
- Formato key=value correcto
- Encoding UTF-8
- Sin caracteres inválidos

## Proceso de Validación
1. Parsear el archivo según su tipo
2. Verificar sintaxis
3. Reportar errores con línea y columna
4. Sugerir correcciones
5. Auto-fix si es posible
