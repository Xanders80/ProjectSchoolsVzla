# API Endpoint Generator

Genera endpoints REST para el School Management System.

## Template
```java
@RestController
@RequestMapping("/api/${resource}")
@PreAuthorize("hasAnyRole(${roles})")
@RequiredArgsConstructor
@Tag(name = "${Entity}", description = "${Entity} API")
public class ${Entity}ApiController {

    private final ${Entity}Service ${entity}Service;

    @GetMapping("/{id}")
    @Operation(summary = "Get ${entity} by ID")
    @ApiResponse(responseCode = "200", description = "Found")
    @ApiResponse(responseCode = "404", description = "Not found")
    public ResponseEntity<${Entity}DTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(${entity}Service.getDTOById(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete ${entity}")
    @ApiResponse(responseCode = "204", description = "Deleted")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ${entity}Service.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}
```
