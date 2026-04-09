# Frontend Specialist - Performance Prompt

## Optimización de Performance Frontend SMS

### Thymeleaf Performance
- Usar `th:fragment` para reutilizar componentes comunes
- Evitar lógica compleja en templates, mover al controller/service
- Usar `th:if` antes de `th:each` para evitar iteraciones innecesarias
- Cachear templates en producción: `spring.thymeleaf.cache=true`

### CSS/JS Optimization
- Usar versiones minificadas en producción (sb-admin-2.min.css, sb-admin-2.min.js)
- Cargar scripts al final del body o con `defer`
- Usar CDN con fallback local para librerías vendor
- Minimizar reflows y repaints

### DataTables Performance
```javascript
// Server-side processing para datasets grandes
$('#dataTable').DataTable({
    "processing": true,
    "serverSide": true,
    "ajax": {
        "url": "/api/students/data",
        "type": "POST"
    },
    "pageLength": 25,
    "lengthMenu": [[10, 25, 50, 100], [10, 25, 50, 100]]
});
```

### Chart.js Performance
- Destruir charts antes de recrearlos: `if (myChart) myChart.destroy()`
- Usar `animation: false` para datasets grandes
- Limitar puntos visibles en gráficos de línea

### Lazy Loading
- Imágenes con `loading="lazy"`
- Cargar scripts solo en páginas que los necesitan
