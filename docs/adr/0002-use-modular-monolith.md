# 0002. Use a Modular Monolith

- Status: Accepted
- Date: 2026-10-04

## Context

- Olly is an open-source platform for live audience interactions: real-time voting, polls, and party-style games where many participants act on a shared session at once.

- These areas change at different rates and have different scaling profiles. Real-time fan-out in particular may eventually need to scale independently of the rest.

- The project is early and has a single maintainer.

## Decision

We will build Olly as a single Gradle application structured as Spring Modulith application modules.
Each module is a business capability, not a technical layer or a pipeline stage.

## Consequences

- Positive: One build, one artifact and one test suite.
- Positive: Boundary violations fail CI instead of piling up.
- Positive: A feature change usually touches one module.
- Neutral: Modules can be setup to scale independently, however work may be needed to configure this.
- Negative: Boundaries rely on discipline or tooling, so they can erode if not enforced.

## Alternatives considered

- **Multi-module Gradle build (olly-core, olly-gateway, olly-api):** duplicates boundaries Modulith already enforces, and deployment roles don't line up with modules.
- **Microservices:** network calls, distributed failure and deployment overhead before any part needs to scale on its own.
