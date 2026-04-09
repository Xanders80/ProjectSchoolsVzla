Batch O Execution Plan

Overview
- Objective: finalize consolidation, produce canonical prompt/template set, and prepare Batch P for production deployment.
- Scope: dedup consolidation, canonical PRD/Tech Spec, caching/observability, system health, runbooks, templates, and integration prompts.

Phases
1. Deduplication & Consolidation (2-4 days)
  - Run comprehensive dedup across all prompts dirs
  - Consolidate PRD/Tech Spec/ADRs into canonical forms
  - Cross-link related prompts to ensure traceability

2. Caching & Observability Finalization (2-3 days)
  - Canonicalize all caching prompts (extensions, dashboards, patterns)
  - Finalize observability prompts and dashboards references
  - Ensure metrics definitions and alert rules are coherent

3. UI Templates & Examples (2 days)
  - Consolidate Handlebars templates and data templates
  - Expand few-shot examples with more realistic scenarios

4. Runbooks & System Prompts (2 days)
  - Finalize runbooks (incident management, deployment, rollback)
  - Consolidate system prompts for runbooks and operationsbrief

5. Integrations (1 day)
  - Confirm Notion/Jira/Slack/Linear prompts and templates or defer to Batch P

6. Validation & Patch Preparation (1 day)
  - Validate against ModeloOpenCode.md
  - Prepare final patch for Batch P and final delivery patch

Deliverables
- Canonical prompts/templates set
- Final consolidation inventory
- Patch ready for Batch P
- Batch P plan if needed

Risks and Mitigations
- Duplicates reappearing: mitigated by canonical consolidation and cross-links
- Missing newer prompts due to scope drift: mitigated via strict review gates
- Integration gaps: mitigated by early Notion/Jira/Slack prompts in Batch P
