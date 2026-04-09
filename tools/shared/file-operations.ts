// File Operations - Shared Tool for SMS
// Provides file reading, writing, searching, and manipulation capabilities

import * as fs from 'fs';
import * as path from 'path';

export class FileOperations {
  private projectRoot: string;

  constructor(projectRoot: string = '.') {
    this.projectRoot = projectRoot;
  }

  async readFile(filePath: string): Promise<string> {
    const fullPath = path.resolve(this.projectRoot, filePath);
    return fs.readFileSync(fullPath, 'utf-8');
  }

  async writeFile(filePath: string, content: string): Promise<void> {
    const fullPath = path.resolve(this.projectRoot, filePath);
    const dir = path.dirname(fullPath);
    if (!fs.existsSync(dir)) {
      fs.mkdirSync(dir, { recursive: true });
    }
    fs.writeFileSync(fullPath, content, 'utf-8');
  }

  async searchFiles(pattern: string, directory: string): Promise<string[]> {
    // Glob pattern matching
    const results: string[] = [];
    const walk = (dir: string) => {
      const entries = fs.readdirSync(dir, { withFileTypes: true });
      for (const entry of entries) {
        const fullPath = path.join(dir, entry.name);
        if (entry.isDirectory()) {
          walk(fullPath);
        } else if (entry.name.match(new RegExp(pattern.replace('*', '.*')))) {
          results.push(fullPath);
        }
      }
    };
    walk(path.resolve(this.projectRoot, directory));
    return results;
  }

  async listDirectory(dirPath: string): Promise<string[]> {
    const fullPath = path.resolve(this.projectRoot, dirPath);
    return fs.readdirSync(fullPath);
  }

  async fileExists(filePath: string): Promise<boolean> {
    const fullPath = path.resolve(this.projectRoot, filePath);
    return fs.existsSync(fullPath);
  }
}
