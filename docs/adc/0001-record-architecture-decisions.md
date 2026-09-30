# 0001. Record architecture decisions 

- Status: Accepted
- Date: 2026-09-29

## Context

Olly is an open-source project with high design complexity.
It is also a passion-project to learn and explore different architecture trade-offs.

## Decision

We will record significant architecture decisions in ADRs.
We will use [Nygard's format] and keep these ADRs small and lightweight.

## Consequences

- Postive: Decision history will be documents and easy to reference next to code.
- Postive: Design decisions will be documented and written rather than blindly accepted.
- Negative: A small writing cost on each significant change.

## Alternatives considered

- **No formal record keeping:** Rationale on decisions will be harder to find, especially for outside contributors. 
- **RFC/enhancement-proposal process (KIP/KEP style):** Too heavy for a single maintner project.
- **MADR Template:** More structure than needed at current size.
