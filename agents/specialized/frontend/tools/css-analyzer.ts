// CSS Analyzer - Frontend Tool for SMS
// Analyzes CSS usage and suggests optimizations

export interface CSSAnalysis {
  unusedClasses: string[];
  duplicateRules: string[];
  specificityIssues: string[];
  suggestions: string[];
}

export class CSSAnalyzer {
  analyze(cssContent: string, htmlContent: string): CSSAnalysis {
    const usedClasses = this.extractUsedClasses(htmlContent);
    const definedClasses = this.extractDefinedClasses(cssContent);

    return {
      unusedClasses: definedClasses.filter(c => !usedClasses.includes(c)),
      duplicateRules: this.findDuplicateRules(cssContent),
      specificityIssues: this.findSpecificityIssues(cssContent),
      suggestions: this.generateSuggestions(usedClasses, definedClasses),
    };
  }

  private extractUsedClasses(html: string): string[] {
    const matches = html.matchAll(/class="([^"]+)"/g);
    const classes: string[] = [];
    for (const match of matches) {
      classes.push(...match[1].split(' '));
    }
    return [...new Set(classes)];
  }

  private extractDefinedClasses(css: string): string[] {
    const matches = css.matchAll(/\.([a-zA-Z0-9_-]+)/g);
    return [...new Set(Array.from(matches, m => m[1]))];
  }

  private findDuplicateRules(css: string): string[] {
    return [];
  }

  private findSpecificityIssues(css: string): string[] {
    return [];
  }

  private generateSuggestions(used: string[], defined: string[]): string[] {
    const suggestions: string[] = [];
    const unused = defined.filter(c => !used.includes(c));
    if (unused.length > 0) {
      suggestions.push(`Remove ${unused.length} unused classes`);
    }
    return suggestions;
  }
}
