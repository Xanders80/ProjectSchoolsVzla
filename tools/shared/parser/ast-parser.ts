// AST Parser - Shared Tool for SMS
// Parses Java code into Abstract Syntax Tree for analysis

export interface ASTNode {
  type: string;
  name?: string;
  children: ASTNode[];
  location?: { line: number; column: number };
}

export interface ParsedClass {
  name: string;
  packageName: string;
  imports: string[];
  annotations: string[];
  fields: ASTNode[];
  methods: ASTNode[];
  superClass?: string;
  interfaces: string[];
}

export class ASTParser {
  parseJava(content: string): ParsedClass {
    const packageName = this.extractPackageName(content);
    const imports = this.extractImports(content);
    const className = this.extractClassName(content);
    const annotations = this.extractAnnotations(content);
    const methods = this.extractMethods(content);
    const fields = this.extractFields(content);

    return {
      name: className,
      packageName,
      imports,
      annotations,
      fields,
      methods,
      methods,
    };
  }

  private extractPackageName(content: string): string {
    const match = content.match(/package\s+([\w.]+)\s*;/);
    return match ? match[1] : '';
  }

  private extractImports(content: string): string[] {
    const matches = content.matchAll(/import\s+([\w.*]+)\s*;/g);
    return Array.from(matches, m => m[1]);
  }

  private extractClassName(content: string): string {
    const match = content.match(/(?:public\s+)?(?:class|interface|enum)\s+(\w+)/);
    return match ? match[1] : '';
  }

  private extractAnnotations(content: string): string[] {
    const matches = content.matchAll(/@(\w+)/g);
    return Array.from(matches, m => m[1]);
  }

  private extractMethods(content: string): ASTNode[] {
    const methods: ASTNode[] = [];
    const regex = /(?:public|private|protected)\s+\w+\s+(\w+)\s*\(/g;
    let match;
    while ((match = regex.exec(content)) !== null) {
      methods.push({ type: 'method', name: match[1], children: [] });
    }
    return methods;
  }

  private extractFields(content: string): ASTNode[] {
    const fields: ASTNode[] = [];
    const regex = /(?:private|protected|public)\s+\w+\s+(\w+)\s*[;=]/g;
    let match;
    while ((match = regex.exec(content)) !== null) {
      fields.push({ type: 'field', name: match[1], children: [] });
    }
    return fields;
  }
}
