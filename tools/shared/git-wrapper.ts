// Git Wrapper - Shared Tool for SMS
// Provides git operations for version control

import { execSync } from 'child_process';

export class GitWrapper {
  private projectRoot: string;

  constructor(projectRoot: string = '.') {
    this.projectRoot = projectRoot;
  }

  runGit(args: string): string {
    return execSync(`git ${args}`, { cwd: this.projectRoot, encoding: 'utf-8' });
  }

  status(): string {
    return this.runGit('status --porcelain');
  }

  diff(files?: string[]): string {
    const fileArgs = files ? files.join(' ') : '';
    return this.runGit(`diff ${fileArgs}`);
  }

  log(limit: number = 10): string {
    return this.runGit(`log --oneline -${limit}`);
  }

  commit(message: string, files?: string[]): string {
    if (files && files.length > 0) {
      this.runGit(`add ${files.join(' ')}`);
    } else {
      this.runGit('add -A');
    }
    return this.runGit(`commit -m "${message}"`);
  }

  branch(): string {
    return this.runGit('branch --show-current');
  }

  createBranch(name: string): string {
    return this.runGit(`checkout -b ${name}`);
  }

  revert(commitHash: string): string {
    return this.runGit(`revert ${commitHash} --no-edit`);
  }

  getRemoteUrl(): string {
    return this.runGit('remote get-url origin');
  }
}
