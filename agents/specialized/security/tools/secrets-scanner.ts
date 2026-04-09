// Secrets Scanner - Security Tool for SMS
// Scans codebase for exposed secrets and credentials

export interface SecretFinding {
  file: string;
  line: number;
  type: string;
  severity: 'critical' | 'high' | 'medium' | 'low';
  description: string;
}

export class SecretsScanner {
  private patterns: Map<string, RegExp> = new Map([
    ['password', /password\s*=\s*['"][^'"]+['"]/gi],
    ['api_key', /api[_-]?key\s*=\s*['"][^'"]+['"]/gi],
    ['secret', /secret\s*=\s*['"][^'"]+['"]/gi],
    ['token', /token\s*=\s*['"][^'"]+['"]/gi],
    ['private_key', /-----BEGIN.*PRIVATE KEY-----/gi],
    ['connection_string', /jdbc:[^'"]+:[^'"]+@/gi],
    ['aws_key', /AKIA[0-9A-Z]{16}/gi],
  ]);

  scan(content: string, filePath: string): SecretFinding[] {
    const findings: SecretFinding[] = [];

    for (const [type, pattern] of this.patterns) {
      const matches = content.matchAll(pattern);
      for (const match of matches) {
        const lineNumber = content.substring(0, match.index).split('\n').length;
        findings.push({
          file: filePath,
          line: lineNumber,
          type,
          severity: this.getSeverity(type),
          description: `Possible ${type} exposed`,
        });
      }
    }

    return findings;
  }

  scanFile(filePath: string): SecretFinding[] {
    return [];
  }

  scanDirectory(dirPath: string): SecretFinding[] {
    return [];
  }

  private getSeverity(type: string): 'critical' | 'high' | 'medium' | 'low' {
    switch (type) {
      case 'private_key':
      case 'aws_key':
        return 'critical';
      case 'password':
      case 'connection_string':
        return 'high';
      case 'api_key':
      case 'secret':
      case 'token':
        return 'medium';
      default:
        return 'low';
    }
  }

  generateReport(findings: SecretFinding[]): string {
    let report = `# Secrets Scan Report\n\n`;
    report += `## Summary\n`;
    report += `Total findings: ${findings.length}\n`;
    report += `Critical: ${findings.filter(f => f.severity === 'critical').length}\n`;
    report += `High: ${findings.filter(f => f.severity === 'high').length}\n`;
    report += `Medium: ${findings.filter(f => f.severity === 'medium').length}\n\n`;

    if (findings.length > 0) {
      report += `## Findings\n`;
      report += `| File | Line | Type | Severity |\n`;
      report += `|------|------|------|----------|\n`;
      for (const f of findings) {
        report += `| ${f.file} | ${f.line} | ${f.type} | ${f.severity} |\n`;
      }
    }

    return report;
  }
}
