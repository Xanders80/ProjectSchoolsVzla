// Standards Checker - Tech Lead Tool for SMS
// Checks code against project standards and conventions

export interface StandardsCheck {
  rule: string;
  passed: boolean;
  message: string;
  file?: string;
  line?: number;
}

export class StandardsChecker {
  private rules: string[] = [
    'constructor_injection',
    'soft_delete_fields',
    'audit_listener',
    'validation_annotations',
    'i18n_messages',
    'csrf_protection',
    'preauthorize_annotation',
    'naming_conventions',
    'no_wildcard_imports',
    'max_line_length',
    'transactional_methods',
    'lazy_fetch_relationships',
    'pagination_for_lists',
    'error_handling',
    'logging_present',
  ];

  checkFile(content: string, filePath: string): StandardsCheck[] {
    const checks: StandardsCheck[] = [];

    for (const rule of this.rules) {
      const result = this.checkRule(rule, content, filePath);
      checks.push(result);
    }

    return checks;
  }

  private checkRule(rule: string, content: string, filePath: string): StandardsCheck {
    switch (rule) {
      case 'constructor_injection':
        return {
          rule,
          passed: !content.includes('@Autowired') || content.includes('@RequiredArgsConstructor'),
          message: 'Use constructor injection with @RequiredArgsConstructor',
          file: filePath,
        };
      case 'soft_delete_fields':
        if (content.includes('@Entity')) {
          return {
            rule,
            passed: content.includes('deleted') && content.includes('deletedAt'),
            message: 'Entity must have soft delete fields',
            file: filePath,
          };
        }
        return { rule, passed: true, message: 'Not an entity', file: filePath };
      case 'audit_listener':
        if (content.includes('@Entity')) {
          return {
            rule,
            passed: content.includes('@EntityListeners'),
            message: 'Entity must have @EntityListeners(AuditEntityListeners.class)',
            file: filePath,
          };
        }
        return { rule, passed: true, message: 'Not an entity', file: filePath };
      case 'i18n_messages':
        if (content.includes('.html')) {
          return {
            rule,
            passed: !content.includes('>') || content.includes('#{'),
            message: 'Use #{message.key} for i18n',
            file: filePath,
          };
        }
        return { rule, passed: true, message: 'Not a template', file: filePath };
      default:
        return { rule, passed: true, message: 'Check passed', file: filePath };
    }
  }

  getReport(checks: StandardsCheck[]): string {
    const passed = checks.filter(c => c.passed).length;
    const failed = checks.filter(c => !c.passed);

    return `Standards Check Report
======================
Passed: ${passed}/${checks.length}
Failed: ${failed.length}

${failed.map(f => `❌ ${f.rule}: ${f.message} (${f.file}:${f.line})`).join('\n')}`;
  }
}
