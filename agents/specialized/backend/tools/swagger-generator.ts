// Swagger Generator - Backend Tool for SMS
// Generates OpenAPI/Swagger documentation for SMS APIs

export interface SwaggerConfig {
  title: string;
  description: string;
  version: string;
  endpoints: Endpoint[];
}

export interface Endpoint {
  method: string;
  path: string;
  summary: string;
  tags: string[];
  responses: Record<number, string>;
}

export class SwaggerGenerator {
  generate(config: SwaggerConfig): string {
    let yaml = `openapi: 3.0.3\n`;
    yaml += `info:\n`;
    yaml += `  title: ${config.title}\n`;
    yaml += `  description: ${config.description}\n`;
    yaml += `  version: ${config.version}\n\n`;
    yaml += `paths:\n`;

    for (const endpoint of config.endpoints) {
      yaml += `  ${endpoint.path}:\n`;
      yaml += `    ${endpoint.method.toLowerCase()}:\n`;
      yaml += `      summary: ${endpoint.summary}\n`;
      yaml += `      tags: [${endpoint.tags.join(', ')}]\n`;
      yaml += `      responses:\n`;
      for (const [code, desc] of Object.entries(endpoint.responses)) {
        yaml += `        ${code}:\n`;
        yaml += `          description: ${desc}\n`;
      }
    }

    return yaml;
  }

  generateForSMS(): string {
    const config: SwaggerConfig = {
      title: 'School Management System API',
      description: 'API documentation for the School Management System',
      version: '1.0.0',
      endpoints: [
        { method: 'GET', path: '/api/students', summary: 'List students', tags: ['students'], responses: { 200: 'List of students' } },
        { method: 'GET', path: '/api/students/{id}', summary: 'Get student by ID', tags: ['students'], responses: { 200: 'Student found', 404: 'Not found' } },
        { method: 'POST', path: '/api/students', summary: 'Create student', tags: ['students'], responses: { 201: 'Created', 400: 'Validation error' } },
        { method: 'PUT', path: '/api/students/{id}', summary: 'Update student', tags: ['students'], responses: { 200: 'Updated', 404: 'Not found' } },
        { method: 'DELETE', path: '/api/students/{id}', summary: 'Soft delete student', tags: ['students'], responses: { 204: 'Deleted', 404: 'Not found' } },
      ],
    };
    return this.generate(config);
  }
}
