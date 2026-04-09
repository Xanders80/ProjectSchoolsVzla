// Migration Creator - Backend Tool for SMS
// Creates database migration files for MariaDB

export interface MigrationConfig {
  version: string;
  description: string;
  operations: MigrationOperation[];
}

export interface MigrationOperation {
  type: 'create_table' | 'alter_table' | 'add_column' | 'drop_column' | 'add_index' | 'add_constraint';
  table: string;
  sql: string;
  rollback: string;
}

export class MigrationCreator {
  create(config: MigrationConfig): string {
    let migration = `-- V${config.version}__${config.description}.sql\n`;
    migration += `-- Description: ${config.description}\n`;
    migration += `-- Date: ${new Date().toISOString()}\n\n`;

    for (const op of config.operations) {
      migration += `-- ${op.type.toUpperCase()}: ${op.table}\n`;
      migration += `${op.sql};\n\n`;
      migration += `-- Rollback:\n`;
      migration += `-- ${op.rollback};\n\n`;
    }

    return migration;
  }

  generateCreateTable(table: string, columns: ColumnDef[]): MigrationOperation {
    const colDefs = columns.map(c => `    ${c.name} ${c.type}${c.nullable ? '' : ' NOT NULL'}`).join(',\n');
    const sql = `CREATE TABLE IF NOT EXISTS ${table} (\n    id BIGINT AUTO_INCREMENT PRIMARY KEY,\n${colDefs},\n    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n    created_by VARCHAR(100),\n    updated_by VARCHAR(100),\n    deleted BOOLEAN DEFAULT FALSE,\n    deleted_at TIMESTAMP NULL\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci`;

    return {
      type: 'create_table',
      table,
      sql,
      rollback: `DROP TABLE IF EXISTS ${table}`,
    };
  }
}

export interface ColumnDef {
  name: string;
  type: string;
  nullable: boolean;
}
