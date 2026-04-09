# Frontend Specialist - Accessibility Prompt

## Accesibilidad en SMS

### Requisitos WCAG 2.1 AA
- Contraste mínimo 4.5:1 para texto normal
- Contraste mínimo 3:1 para texto grande
- Navegación completa por teclado
- Labels asociados a todos los inputs
- Atributos ARIA donde sea necesario

### Prácticas de Accesibilidad
```html
<!-- Labels correctamente asociados -->
<label for="studentName" th:text="#{student.name}">Name</label>
<input type="text" id="studentName" th:field="*{name}" class="form-control" 
       aria-describedby="studentNameHelp" aria-required="true">
<small id="studentNameHelp" class="form-text text-muted" th:text="#{student.name.help}"></small>

<!-- Skip navigation -->
<a href="#main-content" class="sr-only sr-only-focusable">Saltar al contenido principal</a>

<!-- Tablas accesibles -->
<table class="table" aria-label="Lista de estudiantes">
    <caption class="sr-only">Estudiantes registrados en el sistema</caption>
</table>

<!-- Formularios con fieldset -->
<fieldset>
    <legend th:text="#{student.personal.info}">Personal Information</legend>
</fieldset>

<!-- Alertas accesibles -->
<div role="alert" aria-live="assertive" class="alert alert-danger">
    <span th:text="#{error.required}"></span>
</div>
```

### Pruebas de Accesibilidad
- Navegar todo el sitio solo con teclado (Tab, Enter, Escape)
- Verificar con screen reader (NVDA en Windows, VoiceOver en Mac)
- Usar Lighthouse para auditoría automática
- Verificar contraste de colores con herramientas como WebAIM
