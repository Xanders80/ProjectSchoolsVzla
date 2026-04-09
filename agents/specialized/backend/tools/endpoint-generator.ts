// Endpoint Generator - Backend Tool for SMS
// Generates REST endpoints for Spring Boot controllers

export interface EndpointConfig {
  entity: string;
  module: string;
  basePath: string;
  roles: string[];
  operations: string[];
}

export class EndpointGenerator {
  generate(config: EndpointConfig): string {
    let code = `@RestController\n`;
    code += `@RequestMapping("/api/${config.basePath}")\n`;
    code += `@PreAuthorize("hasAnyRole('${config.roles.join("', '")}'))\n";
    code += `@RequiredArgsConstructor\n`;
    code += `@Tag(name = "${config.entity}", description = "${config.entity} API")\n`;
    code += `public class ${config.entity}ApiController {\n\n`;
    code += `    private final ${config.entity}Service ${config.entity.toLowerCase()}Service;\n\n`;

    if (config.operations.includes('list')) {
      code += this.generateListEndpoint(config);
    }
    if (config.operations.includes('get')) {
      code += this.generateGetEndpoint(config);
    }
    if (config.operations.includes('create')) {
      code += this.generateCreateEndpoint(config);
    }
    if (config.operations.includes('update')) {
      code += this.generateUpdateEndpoint(config);
    }
    if (config.operations.includes('delete')) {
      code += this.generateDeleteEndpoint(config);
    }

    code += `}\n`;
    return code;
  }

  private generateListEndpoint(config: EndpointConfig): string {
    return `    @GetMapping\n    @Operation(summary = "List all ${config.entity.toLowerCase()}")\n    public ResponseEntity<Page<${config.entity}DTO>> list(@PageableDefault(size = 25) Pageable pageable) {\n        return ResponseEntity.ok(${config.entity.toLowerCase()}Service.findAll(pageable));\n    }\n\n`;
  }

  private generateGetEndpoint(config: EndpointConfig): string {
    return `    @GetMapping("/{id}")\n    @Operation(summary = "Get ${config.entity.toLowerCase()} by ID")\n    public ResponseEntity<${config.entity}DTO> getById(@PathVariable Long id) {\n        return ResponseEntity.ok(${config.entity.toLowerCase()}Service.getDTOById(id));\n    }\n\n`;
  }

  private generateCreateEndpoint(config: EndpointConfig): string {
    return `    @PostMapping\n    @Operation(summary = "Create ${config.entity.toLowerCase()}")\n    public ResponseEntity<${config.entity}DTO> create(@Valid @RequestBody ${config.entity}DTO dto) {\n        return ResponseEntity.status(HttpStatus.CREATED).body(${config.entity.toLowerCase()}Service.create(dto));\n    }\n\n`;
  }

  private generateUpdateEndpoint(config: EndpointConfig): string {
    return `    @PutMapping("/{id}")\n    @Operation(summary = "Update ${config.entity.toLowerCase()}")\n    public ResponseEntity<${config.entity}DTO> update(@PathVariable Long id, @Valid @RequestBody ${config.entity}DTO dto) {\n        return ResponseEntity.ok(${config.entity.toLowerCase()}Service.update(id, dto));\n    }\n\n`;
  }

  private generateDeleteEndpoint(config: EndpointConfig): string {
    return `    @DeleteMapping("/{id}")\n    @Operation(summary = "Soft delete ${config.entity.toLowerCase()}")\n    public ResponseEntity<Void> delete(@PathVariable Long id) {\n        ${config.entity.toLowerCase()}Service.softDelete(id);\n        return ResponseEntity.noContent().build();\n    }\n\n`;
  }
}
