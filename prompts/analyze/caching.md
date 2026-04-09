# Caching Analysis Prompt

Goal: Identify caching opportunities in the School Management System (SMS).

Inputs:
- Current cache configuration (if any)
- Frequent read-heavy entities (e.g., Students, Courses, Grades)
- Access patterns and data staleness tolerance

Outputs:
- Caching strategy recommendations
- Items to cache (entity level, query results, DTOs)
- Invalidation policy and cache eviction rules

Guidance:
- Prefer cache for read-mostly data with reasonable staleness tolerance
- Use appropriate cache granularity: entities, DTOs, or query results
- Consider cache invalidation strategies on writes (post-commit, cache-aside, write-through)
- Document required cache headers or TTLs
