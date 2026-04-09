// Dependency Parser - Shared Tool for SMS
// Analyzes dependencies between Java files and modules

export interface Dependency {
  from: string;
  to: string;
  type: 'import' | 'extends' | 'implements' | 'injects';
}

export interface ModuleDependency {
  fromModule: string;
  toModule: string;
  count: number;
  dependencies: Dependency[];
}

export class DependencyParser {
  parseDependencies(files: Map<string, string>): Dependency[] {
    const dependencies: Dependency[] = [];

    for (const [filePath, content] of files) {
      const imports = this.extractImports(content);
      for (const imp of imports) {
        if (imp.startsWith('com.school')) {
          dependencies.push({
            from: filePath,
            to: imp.replace(/\./g, '/') + '.java',
            type: 'import',
          });
        }
      }

      const injections = this.extractInjections(content);
      for (const inj of injections) {
        dependencies.push({
          from: filePath,
          to: inj,
          type: 'injects',
        });
      }
    }

    return dependencies;
  }

  detectCircularDependencies(dependencies: Dependency[]): string[][] {
    const graph = new Map<string, Set<string>>();
    for (const dep of dependencies) {
      if (!graph.has(dep.from)) graph.set(dep.from, new Set());
      graph.get(dep.from)!.add(dep.to);
    }

    const cycles: string[][] = [];
    const visited = new Set<string>();
    const stack = new Set<string>();

    const dfs = (node: string, path: string[]) => {
      if (stack.has(node)) {
        const cycleStart = path.indexOf(node);
        cycles.push(path.slice(cycleStart).concat(node));
        return;
      }
      if (visited.has(node)) return;

      visited.add(node);
      stack.add(node);
      path.push(node);

      for (const neighbor of graph.get(node) || []) {
        dfs(neighbor, [...path]);
      }

      stack.delete(node);
    };

    for (const node of graph.keys()) {
      dfs(node, []);
    }

    return cycles;
  }

  private extractImports(content: string): string[] {
    const matches = content.matchAll(/import\s+([\w.]+)\s*;/g);
    return Array.from(matches, m => m[1]);
  }

  private extractInjections(content: string): string[] {
    const matches = content.matchAll(/private\s+final\s+(\w+)\s+\w+/g);
    return Array.from(matches, m => m[1]);
  }
}
