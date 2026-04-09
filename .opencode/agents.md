# OpenCode Agents Documentation
# School Management System

## Agent Overview

This project uses a multi-agent architecture for AI-assisted development, organized in three tiers:

### Tier 1: Core Agents
- **Software Architect** (`agents/core/architect/`) - System architecture design, ADRs, pattern definition
- **Product Manager** (`agents/core/product-manager/`) - Requirements, user stories, PRDs
- **Tech Lead** (`agents/core/tech-lead/`) - Code reviews, technical decisions, mentoring

### Tier 2: Specialized Agents
- **Frontend Specialist** (`agents/specialized/frontend/`) - Thymeleaf templates, CSS, JS, SB Admin 2
- **Backend Specialist** (`agents/specialized/backend/`) - Java 21, Spring Boot 3.5, JPA, REST APIs
- **DevOps Specialist** (`agents/specialized/devops/`) - Maven, Docker, CI/CD, deployment
- **Security Specialist** (`agents/specialized/security/`) - Spring Security, rate limiting, anomaly detection
- **QA Specialist** (`agents/specialized/qa/`) - Unit tests, integration tests, E2E

### Tier 3: Orchestrators
- **Master Orchestrator** (`agents/orchestrators/master/`) - Task routing, coordination, synthesis
- **Context Manager** (`agents/orchestrators/context-manager/`) - Context pruning, priority management

## Project-Specific Context

### Technology Stack
- **Language:** Java 21
- **Framework:** Spring Boot 3.5.10
- **Database:** MariaDB (production), H2 (testing)
- **Template Engine:** Thymeleaf with Spring Security extras
- **UI:** SB Admin 2 (Bootstrap 4, jQuery, DataTables, Chart.js)
- **Build:** Maven
- **API Docs:** SpringDoc OpenAPI 2.2.0
- **PDF:** OpenHTMLToPDF, iText7
- **Excel:** Apache POI
- **Testing:** Spring Boot Test, Security Test

### Package Structure
```
com.school
├── academic/      - Academic domain (20 entities, 22 services, 20 repos)
├── admin/         - Administration (Staff entity)
├── bi/            - Business Intelligence
├── communication/ - Messaging, forums, notifications
├── core/          - Cross-cutting (security, config, audit, validation)
├── finance/       - Payments, fees
├── health/        - Medical records, vaccines
├── hr/            - Contracts, payroll, staff attendance
├── infra/         - Assets, buildings, rooms, maintenance, labs
├── library/       - Books, loans, digital resources
├── report/        - Reporting services
├── schedule/      - Scheduling
└── web/           - All controllers organized by domain
```

### Architecture Patterns
- Layered Architecture (MVC)
- Domain-Driven Module Organization
- Soft deletes with `deleted`/`deletedAt` fields
- Audit trail via `AuditEntityListener` + `AuditLog`
- Role-based access control (ADMIN, STAFF, DIRECTOR, PARENT)
- Rate limiting and anomaly detection filters
- Interface + Implementation pattern for services
- `Person` as `@MappedSuperclass` for `Student` and `Staff`

### Conventions
- Controllers in `com.school.web.controller.{domain}`
- Entities, Repositories, Services in domain packages
- Validation groups: `ValidationGroups.Create/Update`
- Internationalization: English and Spanish
- Profile-based configuration: dev, test, h2, prod
- Caching: ConcurrentMapCacheManager (users, roles, courses, students)
