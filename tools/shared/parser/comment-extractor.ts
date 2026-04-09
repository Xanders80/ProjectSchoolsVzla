// Comment Extractor - Shared Tool for SMS
// Extracts and analyzes comments from Java code

export interface Comment {
  type: 'javadoc' | 'line' | 'block';
  content: string;
  line: number;
  target?: string;
}

export class CommentExtractor {
  extractComments(content: string): Comment[] {
    const comments: Comment[] = [];
    const lines = content.split('\n');

    lines.forEach((line, index) => {
      // Javadoc
      if (line.trim().startsWith('/**')) {
        const javadoc = this.extractJavadoc(lines, index);
        comments.push(javadoc);
      }
      // Line comment
      else if (line.trim().startsWith('//')) {
        comments.push({
          type: 'line',
          content: line.trim().replace(/^\/\/\s*/, ''),
          line: index + 1,
        });
      }
      // Block comment
      else if (line.trim().startsWith('/*')) {
        comments.push({
          type: 'block',
          content: line.trim().replace(/^\/\*\s*|\s*\*\/$/g, ''),
          line: index + 1,
        });
      }
    });

    return comments;
  }

  private extractJavadoc(lines: string[], startIndex: number): Comment {
    let content = '';
    let i = startIndex;
    while (i < lines.length && !lines[i].trim().endsWith('*/')) {
      content += lines[i].trim().replace(/^\/\*\*\s*|\s*\*\/$|\s*\*\s?/g, '') + '\n';
      i++;
    }
    return { type: 'javadoc', content: content.trim(), line: startIndex + 1 };
  }

  getCommentCoverage(content: string): number {
    const comments = this.extractComments(content);
    const lines = content.split('\n').filter(l => l.trim() && !l.trim().startsWith('//')).length;
    const commentLines = comments.reduce((sum, c) => sum + c.content.split('\n').length, 0);
    return Math.min(100, (commentLines / (lines + commentLines)) * 100);
  }
}
