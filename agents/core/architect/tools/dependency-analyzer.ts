// Dependency Analyzer - Architect Tool for SMS
// Analyzes module dependencies for the School Management System

export interface ModuleDependency {
  from: string;
  to: string;
  type: 'direct' | 'transitive';
  strength: 'strong' | 'weak';
}

export class DependencyAnalyzer {
  private modules = ['academic', 'admin', 'bi', 'communication', 'core', 'finance', 'health', 'hr', 'infra', 'library', 'report', 'schedule'];

  analyzeDependencies(): ModuleDependency[] {
    return [
      { from: 'academic', to: 'core', type: 'direct', strength: 'strong' },
      { from: 'academic', to: 'schedule', type: 'direct', strength: 'strong' },
      { from: 'academic', to: 'communication', type: 'direct', strength: 'weak' },
      { from: 'finance', to: 'academic', type: 'direct', strength: 'strong' },
      { from: 'finance', to: 'core', type: 'direct', strength: 'strong' },
      { from: 'hr', to: 'core', type: 'direct', strength: 'strong' },
      { from: 'hr', to: 'finance', type: 'direct', strength: 'weak' },
      { from: 'health', to: 'academic', type: 'direct', strength: 'strong' },
      { from: 'health', to: 'core', type: 'direct', strength: 'strong' },
      { from: 'library', to: 'academic', type: 'direct', strength: 'strong' },
      { from: 'library', to: 'core', type: 'direct', strength: 'strong' },
      { from: 'bi', to: 'academic', type: 'transitive', strength: 'weak' },
      { from: 'bi', to: 'finance', type: 'transitive', strength: 'weak' },
      { from: 'bi', to: 'hr', type: 'transitive', strength: 'weak' },
      { from: 'report', to: 'academic', type: 'transitive', strength: 'weak' },
      { from: 'report', to: 'finance', type: 'transitive', strength: 'weak' },
      { from: 'infra', to: 'core', type: 'direct', strength: 'strong' },
      { from: 'communication', to: 'core', type: 'direct', strength: 'strong' },
      { from: 'communication', to: 'academic', type: 'direct', strength: 'weak' },
    ];
  }

  detectCircularDependencies(deps: ModuleDependency[]): string[][] {
    const graph = new Map<string, Set<string>>();
    for (const dep of deps) {
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

  getDependencyGraph(): string {
    return `graph TD
    core["core"]
    academic["academic"]
    admin["admin"]
    bi["bi"]
    communication["communication"]
    finance["finance"]
    health["health"]
    hr["hr"]
    infra["infra"]
    library["library"]
    report["report"]
    schedule["schedule"]

    academic --> core
    academic --> schedule
    academic --> communication
    finance --> academic
    finance --> core
    hr --> core
    hr --> finance
    health --> academic
    health --> core
    library --> academic
    library --> core
    bi --> academic
    bi --> finance
    bi --> hr
    report --> academic
    report --> finance
    infra --> core
    communication --> core
    communication --> academic
    admin --> core`;
  }
}
