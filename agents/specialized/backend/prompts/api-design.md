# Backend Specialist - API Design Prompt

## Diseño de APIs para SMS

### Controllers Thymeleaf (Server-Side Rendering)
```java
@Controller
@RequestMapping("/students")
@PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
public class StudentController {
    
    @GetMapping
    public String list(Model model, Pageable pageable) {
        model.addAttribute("students", studentService.findAll(pageable));
        return "academic/students/list";
    }
    
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("student", new Student());
        return "academic/students/form";
    }
    
    @PostMapping
    public String create(@Valid @ModelAttribute Student student, 
                         BindingResult result, RedirectAttributes redirect) {
        if (result.hasErrors()) return "academic/students/form";
        studentService.save(student);
        redirect.addFlashAttribute("success", "student.created");
        return "redirect:/students";
    }
}
```

### Controllers REST (APIs JSON)
```java
@RestController
@RequestMapping("/api/students")
@PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
public class StudentApiController {
    
    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getDTOById(id));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentService.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}
```

### Reglas de Diseño
1. **Thymeleaf Controllers:** Usar `@Controller`, retornar nombres de templates
2. **API Controllers:** Usar `@RestController`, retornar `ResponseEntity`
3. **Validación:** Siempre `@Valid` + `BindingResult` para formularios
4. **Soft Delete:** Nunca eliminar físicamente, usar `deleted = true`
5. **Auditoría:** Registrar acciones significativas via `AuditService`
6. **Paginación:** Usar `Pageable` para listas, retornar `Page<T>`
7. **Mensajes:** Usar keys de i18n, no textos hardcodeados

### Endpoints por Módulo
Cada módulo sigue el patrón CRUD completo:
- `GET /{resource}` - Lista con paginación
- `GET /{resource}/new` - Formulario de creación
- `POST /{resource}` - Crear recurso
- `GET /{resource}/{id}` - Ver detalle
- `GET /{resource}/{id}/edit` - Formulario de edición
- `POST /{resource}/{id}` - Actualizar recurso
- `POST /{resource}/{id}/delete` - Soft delete
