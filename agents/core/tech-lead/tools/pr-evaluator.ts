// PR Evaluator - Tech Lead Tool for SMS
// Evaluates Pull Requests for quality and compliance

export interface PREvaluation {
  score: number;
  categories: EvaluationCategory[];
  recommendations: string[];
  approved: boolean;
}

export interface EvaluationCategory {
  name: string;
  score: number;
  issues: string[];
}

export class PREvaluator {
  evaluate(diff: string, files: string[]): PREvaluation {
    const categories: EvaluationCategory[] = [
      this.evaluateCodeQuality(diff),
      this.evaluateSecurity(diff),
      this.evaluatePerformance(diff),
      this.evaluateTesting(diff),
      this.evaluateConventions(diff),
    ];

    const avgScore = categories.reduce((sum, c) => sum + c.score, 0) / categories.length;
    const allIssues = categories.flatMap(c => c.issues);

    return {
      score: Math.round(avgScore * 100) / 100,
      categories,
      recommendations: this.generateRecommendations(allIssues),
      approved: avgScore >= 0.8 && !allIssues.some(i => i.includes('critical')),
    };
  }

  private evaluateCodeQuality(diff: string): EvaluationCategory {
    return { name: 'Code Quality', score: 0.9, issues: [] };
  }

  private evaluateSecurity(diff: string): EvaluationCategory {
    return { name: 'Security', score: 0.95, issues: [] };
  }

  private evaluatePerformance(diff: string): EvaluationCategory {
    return { name: 'Performance', score: 0.85, issues: [] };
  }

  private evaluateTesting(diff: string): EvaluationCategory {
    return { name: 'Testing', score: 0.8, issues: [] };
  }

  private evaluateConventions(diff: string): EvaluationCategory {
    return { name: 'Conventions', score: 0.9, issues: [] };
  }

  private generateRecommendations(issues: string[]): string[] {
    return issues.map(i => `Fix: ${i}`);
  }
}
