# Frontend Specialist - Testing Prompt

## Testing Frontend SMS

### Testing de Templates Thymeleaf
- Verificar que todos los mensajes usan `#{key}` para i18n
- Confirmar que los formularios incluyen CSRF tokens
- Validar que los enlaces usan `th:href="@{/path}"`
- Verificar fragments se incluyen correctamente

### Testing de JavaScript
```javascript
// Test de validación de formularios
describe('Form Validation', () => {
    it('should show error for empty required field', () => {
        // Arrange
        const form = document.getElementById('studentForm');
        const input = document.getElementById('firstName');
        
        // Act
        input.value = '';
        form.dispatchEvent(new Event('submit'));
        
        // Assert
        expect(input.classList).toContain('is-invalid');
    });
});
```

### Testing Manual
- Probar en Chrome, Firefox, Safari, Edge
- Verificar responsive en móvil, tablet, desktop
- Probar con JavaScript deshabilitado (degradación graceful)
- Verificar accesibilidad con teclado y screen reader

### Checklist de QA Frontend
- [ ] Todos los formularios validan client-side y server-side
- [ ] Mensajes de error se muestran correctamente
- [ ] DataTables funciona con búsqueda, paginación y ordenamiento
- [ ] Charts se renderizan correctamente
- [ ] Sidebar navigation funciona en móvil
- [ ] Delete modals confirman antes de eliminar
- [ ] Flash messages se muestran y desaparecen
- [ ] No hay errores en la consola del navegador
