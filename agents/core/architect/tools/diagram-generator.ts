// Diagram Generator - Architect Tool for SMS
// Generates architecture diagrams for the School Management System

export interface DiagramConfig {
  type: 'component' | 'class' | 'sequence' | 'deployment';
  format: 'plantuml' | 'mermaid' | 'graphviz';
  modules?: string[];
  includeDetails: boolean;
}

export class DiagramGenerator {
  generateComponentDiagram(config: DiagramConfig): string {
    if (config.format === 'mermaid') {
      return this.generateMermaidComponent(config);
    }
    return this.generatePlantUMLComponent(config);
  }

  generateClassDiagram(entities: string[], module: string): string {
    let diagram = 'classDiagram\n';
    for (const entity of entities) {
      diagram += `  class ${entity} {\n`;
      diagram += `    +Long id\n`;
      diagram += `    +Boolean deleted\n`;
      diagram += `    +LocalDateTime createdAt\n`;
      diagram += `    +LocalDateTime updatedAt\n`;
      diagram += `  }\n`;
    }
    return diagram;
  }

  private generateMermaidComponent(config: DiagramConfig): string {
    return `graph TD
    subgraph SMS["School Management System"]
        subgraph Web["Web Layer"]
            Controllers[Controllers]
            Templates[Thymeleaf Templates]
        end
        subgraph Service["Service Layer"]
            Services[Services]
        end
        subgraph Data["Data Layer"]
            Repos[Repositories]
            DB[(MariaDB)]
        end
    end
    Controllers --> Services
    Services --> Repos
    Repos --> DB
    Controllers --> Templates`;
  }

  private generatePlantUMLComponent(config: DiagramConfig): string {
    return `@startuml
package "Web Layer" {
  [Controllers]
  [Thymeleaf Templates]
}
package "Service Layer" {
  [Services]
}
package "Data Layer" {
  [Repositories]
  database "MariaDB" as DB
}
Controllers --> Services
Services --> Repositories
Repositories --> DB
@enduml`;
  }
}
