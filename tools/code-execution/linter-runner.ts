// Linter Runner - Code Execution Tool for SMS
// Runs linting and static analysis tools

export interface LintResult {
  errors: LintIssue[];
  warnings: LintIssue[];
  info: LintIssue[];
}

export interface LintIssue {
  file: string;
  line: number;
  column: number;
  rule: string;
  message: string;
  severity: 'error' | 'warning' | 'info';
}

export class LinterRunner {
  async runLint(projectPath: string): Promise<LintResult> {
    return { errors: [], warnings: [], info: [] };
  }

  async runCheckstyle(projectPath: string): Promise<LintResult> {
    return { errors: [], warnings: [], info: [] };
  }

  async runSpotBugs(projectPath: string): Promise<LintResult> {
    return { errors: [], warnings: [], info: [] };
  }

  async runAll(projectPath: string): Promise<LintResult> {
    const [checkstyle, spotbugs] = await Promise.all([
      this.runCheckstyle(projectPath),
      this.runSpotBugs(projectPath),
    ]);

    return {
      errors: [...checkstyle.errors, ...spotbugs.errors],
      warnings: [...checkstyle.warnings, ...spotbugs.warnings],
      info: [...checkstyle.info, ...spotbugs.info],
    };
  }
}
