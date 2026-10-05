# 0003. Declare deployment topology explicitly

- Status: Accepted
- Date: 2026-10-04

## Context

The same jar must run either as a single process with no external infrastructure, or as a cluster where `api`, `gateway` and `core` run on separate instances.
The in-memory pub/sub and event log only work when every role runs in one process.
## Decision

Topology is configured with typed properties bound to `OllyProperties`.
Startup will fail in configuration fails rules set in `OllyProperties`.

## Consequences

- Positive: Olly has defaults, and can start with no configuration.
- Positive: A partially configured cluster fails at startup with the fix in the error message, instead of losing updates at runtime.
- Positive: One artifact serves every topology, and roles can be combined freely on an instance.
- Negative: Validation only sees the local instance.
  Replicas left on the default `standalone` mode, or a cluster where no instance runs `core`, still start without error.
  Catching these needs cross-instance coordination, planned as a database lease check with cluster mode.

## Alternatives considered

- **Infer mode from roles or backends:** Can easily setup incorrect/bad configurations, as there won't be enforcement. 
