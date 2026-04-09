# Security & Performance Analysis Prompt

Goal: Evaluate the security posture and performance hotspots of the SMS.

Inputs:
- Current security posture (threat model, controls)
- Performance metrics (latency, throughput, cache hit rate)
- List of known bottlenecks or hot paths

Outputs:
- Security remediation plan (short and long-term)
- Performance optimization plan with prioritized actions
- Risk assessment and risk mitigation roadmap

Guidance:
- Prioritize high-risk findings and quick wins
- Suggest caching strategies for read-heavy data with TTLs
- Recommend architectural adjustments if needed (e.g., distributed cache, API rate limiting)
- Document test plan to verify fixes
