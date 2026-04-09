# Caching Scenario

- Scenario: Read-heavy endpoint for student catalog with frequent queries
- Objective: Identify best cache strategy (entity vs query result) and TTLs
- Constraints: Data accuracy within TTL, invalidate on write
- Expected output: recommended cache regions and invalidation events
