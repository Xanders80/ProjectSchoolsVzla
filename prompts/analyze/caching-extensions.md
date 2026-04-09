# Advanced Caching Extensions

- Canonical version: Batch M
- This document is the canonical reference for caching extension strategies used in Batch M.

- Expand on distributed caching strategies (Redis cluster, multi-node setups)
- TTL granularity by entity and query result with invalidation rules
- Use regional caches for geographic distribution
- Cache invalidation triggers on writes at different isolation levels
- Observability: cache hit rate, eviction reasons, stale data detection
- Migration plan: from in-memory to distributed cache with minimal downtime
