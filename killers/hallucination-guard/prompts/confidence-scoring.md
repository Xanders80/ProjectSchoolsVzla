# Hallucination Guard - Confidence Scoring Prompt

## Sistema de Scoring de Confianza

### Factores de Scoring

#### Syntax Correctness (0-30 puntos)
- 30: Compila sin errores
- 20: Errores menores de sintaxis
- 10: Errores significativos
- 0: No compila

#### API Correctness (0-25 puntos)
- 25: Todas las APIs verificadas en documentación
- 20: APIs probables pero no verificadas
- 10: Algunas APIs dudosas
- 0: APIs inventadas

#### Context Consistency (0-25 puntos)
- 25: Totalmente consistente con el proyecto
- 20: Mayormente consistente
- 10: Algunas inconsistencias
- 0: Inconsistente con el proyecto

#### Security (0-20 puntos)
- 20: Sin riesgos de seguridad
- 15: Riesgos menores
- 5: Riesgos significativos
- 0: Código peligroso

### Interpretación del Score
- 90-100: Aplicar directamente
- 70-89: Aplicar con revisión
- 50-69: Revisión humana requerida
- 0-49: Rechazar y regenerar
