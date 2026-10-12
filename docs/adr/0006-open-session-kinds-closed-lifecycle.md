# 0006. Open session kinds, closed lifecycle categories

- Status: Accepted
- Date: 2026-10-11

## Context

Olly is currently expected to have three session kinds: poll, quiz and prompt.
More are expected, from Olly itself and from third-party plugins through the game SPI.
Each kind also has its own phases, such as a poll's draft, open and closing, or a prompt room's lobby, voting and reveal.
Platform features (joining, closing, cleanup, listing live sessions) must work the same for every kind, including kinds that don't exist yet.

## Decision

`kind` is an open set of string identifiers.
Built-in kinds use plain names (`poll`, `quiz`, `prompt`); plugin kinds are namespaced (`acme.bingo`) so independent authors can't collide.
Each kind is implemented by a `SessionType` registered in a registry, and an unknown kind is rejected at the API with the list of available kinds.
The database stores `kind` as `VARCHAR(64)` without a `CHECK` constraint, so adding a kind never needs a migration.
`state` is the session's lifecycle category (`OPEN`, `CLOSED`), owned by the platform, enforced by a `CHECK` constraint, and the only value platform features use.
Kind-specific phases are owned by each kind, map to a lifecycle category, and are stored where that kind keeps its state.
The API documents `kind` as open (clients must handle unknown values) and `state` as frozen, and exposes the available kinds through a discovery endpoint.

## Consequences

- Positive: New kinds, built-in or plugin, need no schema or platform change.
- Positive: Platform features and queries depend only on a small, indexed category, however many kinds exist.
- Positive: Clients can build their UI from the discovery endpoint instead of hard-coding kinds.
- Neutral: Kinds composed from pipeline stages as configuration are planned once the pipeline exists, using the same registry.
- Negative: The compiler can no longer check that every kind is handled; behaviour goes through each kind's `SessionType` instead of a `switch`.
- Negative: Adding a lifecycle category is a deliberate platform change with a migration.

## Alternatives considered

- **Closed enums with `CHECK` constraints for both:** compiler-checked, but only Olly releases could add kinds.
- **A lookup table per value set:** a second copy of values the code already defines, and still a migration per kind.
- **Database enum types:** not portable between H2 and PostgreSQL, and PostgreSQL can't remove enum values.
- **One open `state` per kind with no shared category:** maximum freedom, but every platform feature would need to know every kind's states.
