// Search Engine - Shared Tool for SMS
// Provides code search and navigation capabilities

import * as fs from 'fs';
import * as path from 'path';

export class SearchEngine {
  private projectRoot: string;

  constructor(projectRoot: string = '.') {
    this.projectRoot = projectRoot;
  }

  searchContent(pattern: string, includePattern: string = '*.java'): SearchResult[] {
    const results: SearchResult[] = [];
    const files = this.findFiles(includePattern);

    for (const file of files) {
      const content = fs.readFileSync(file, 'utf-8');
      const lines = content.split('\n');

      lines.forEach((line, index) => {
        if (line.match(new RegExp(pattern, 'i'))) {
          results.push({
            file: path.relative(this.projectRoot, file),
            line: index + 1,
            content: line.trim(),
          });
        }
      });
    }

    return results;
  }

  findFiles(pattern: string): string[] {
    const results: string[] = [];
    const walk = (dir: string) => {
      const entries = fs.readdirSync(dir, { withFileTypes: true });
      for (const entry of entries) {
        const fullPath = path.join(dir, entry.name);
        if (entry.isDirectory()) {
          if (entry.name !== 'target' && entry.name !== '.git' && entry.name !== 'node_modules') {
            walk(fullPath);
          }
        } else {
          const regex = new RegExp('^' + pattern.replace(/\*/g, '.*') + '$');
          if (regex.test(entry.name)) {
            results.push(fullPath);
          }
        }
      }
    };
    walk(this.projectRoot);
    return results;
  }

  findClass(className: string): string | null {
    const files = this.findFiles('*.java');
    for (const file of files) {
      const content = fs.readFileSync(file, 'utf-8');
      if (content.includes(`class ${className}`) || content.includes(`interface ${className}`)) {
        return path.relative(this.projectRoot, file);
      }
    }
    return null;
  }

  findMethod(methodName: string): SearchResult[] {
    return this.searchContent(`\\b${methodName}\\s*\\(`, '*.java');
  }
}

interface SearchResult {
  file: string;
  line: number;
  content: string;
}
