# Dead Code Eliminator Prompt

Identifica y elimina código muerto en el School Management System.

## Detección de Código Muerto

### 1. Métodos No Usados
```bash
# IDE analysis o grep inverso
grep -r "methodName" src/ --include="*.java" | wc -l
```

### 2. Clases No Usadas
- Controllers sin rutas accesibles
- Services no inyectados
- Entities sin repositorios

### 3. Imports No Usados
```bash
mvn clean compile 2>&1 | grep "unused import"
```

### 4. Variables No Usadas
- Variables declaradas pero no leídas
- Parámetros no utilizados
- Campos no referenciados

### 5. Código Inalcanzable
- Código después de return/throw
- Branches nunca ejecutados
- Código comentado sin propósito

## Proceso de Eliminación
1. Identificar código muerto
2. Verificar que no es usado dinámicamente
3. Verificar que no es usado por reflection
4. Eliminar código
5. Ejecutar tests para verificar
6. Commit con mensaje descriptivo

## Precauciones para SMS
- No eliminar endpoints protegidos por feature flags
- No eliminar código usado por reflection
- No eliminar código de migración
- Verificar con tests antes de eliminar
