# 0007. Put infrastructure behind ports with two adapters each

- Status: Accepted
- Date: 2026-10-13

## Context

Standalone mode must run with no infrastructure.
Later on we must support Redis and Kafka to replace in-memory and for cluster support.
Both modes must behave the same, and it must easy to prove and enforce this. 
The modules that need infrastructure should require dependence on Redis or Kafka code directly.

## Decision

Each infrastructure need is a port: an interface in the module that uses it, in a sub-package that Spring Modulith treats as internal.
The first ports are `PubSub` (realtime), `VoteLog` and `TallyView` (voting).
Each port has an in-memory adapter for standalone mode and a cluster adapter for cluster mode.
A configuration class picks the adapter with an exhaustive `switch` over the `OllyProperties` enums, so adding a backend fails compilation until every port handles it.
Each port has an abstract `…Contract` test suite that every adapter extends.
Contract tests prove that something did not arrive by waiting for a later marker message, not by sleeping, so the same tests work for asynchronous adapters.

## Consequences

- Positive: Standalone mode needs no infrastructure, and its tests run in milliseconds.
- Positive: Cluster adapters must pass the same tests as the in-memory ones.
- Positive: Only the module that owns a port can use it.
- Neutral: Selecting a cluster backend fails at startup until its adapter exists in Phase 4.
- Negative: A port can only offer what every adapter can provide.
- Negative: In-memory adapters cannot reveal distributed failures; those need separate chaos tests.

## Alternatives considered

- **`@ConditionalOnProperty` per adapter:** matches strings, so a value such as `IN_MEMORY` silently registers no bean, and the compiler can't check that every backend is covered.
- **A shared `infrastructure` module:** every module would depend on it, and ports would no longer belong to the module that needs them.
- **Calling Redis and Kafka directly:** standalone mode would be impossible, and tests would need containers everywhere.
