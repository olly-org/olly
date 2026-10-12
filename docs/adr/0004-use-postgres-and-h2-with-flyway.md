# 0004. Use PostgreSQL and H2 with Flyway migrations

- Status: Accepted
- Date: 2026-10-11

## Context

Olly stores tenants and sessions now.
Interactions and final results will follow, each added with the code that uses it.
This data is relational, tenant-scoped and written at low volume.
Votes never go to the database in either mode: they go through the event log, and only each poll's final result is stored, so the database is off the hot path.
Standalone mode must be able to run with no external infrastructure.
Cluster mode must allow rolling deploys, so old and new code will run against the same schema for a while.

## Decision

H2 is the default when no database is configured, so standalone mode needs no setup.
PostgreSQL is supported in any mode, recommended for production, and required in cluster mode.
The JDBC URL alone decides which database is used; there is no separate Olly setting for it.
Flyway is the only way the schema changes.
Migrations live in one folder and are named `V<n>__<module>_<verb>_<object>.sql`, so the run order and the owning module are both visible.
Migrations use portable SQL, and `AbstractMigrationTests` runs the same checks on H2 and on PostgreSQL in Testcontainers on every build.
Timestamps are `TIMESTAMP WITH TIME ZONE` holding UTC instants.
`updated_at` is set by the application, because triggers are not portable between H2 and PostgreSQL.
Merged migrations are never edited; schema changes are split into an additive release and a later removal release.
Naming rules live in `src/main/resources/db/migration/README.md`.

## Consequences

- Positive: Transactions, foreign keys and named constraints protect tenant data on both databases.
- Positive: Standalone mode needs no database server.
- Positive: A standalone instance starts with one command, which keeps local development fast.
- Positive: The file listing shows both run order and owning module.
- Negative: PostgreSQL-only features such as `JSONB`, partial indexes and triggers are off-limits.
- Negative: Renames and removals take two releases.
- Negative: CI needs Docker for the PostgreSQL tests.

## Alternatives considered

- **Cassandra:** Olly doesn't need the benefits (write throughput) on this path, and no joins or transactions; it stays an option for a future vote-archive adapter.
- **PostgreSQL only:** standalone mode would need a database server.
- **One migration folder per module:** versions are global, so folders would show gaps and suggest an independence the shared sequence doesn't have.
- **Liquibase changelog:** explicit ordering and rollbacks, but another DSL and a source-available license.
- **Declarative schema tools (for example Atlas):** one desired-state file, but an extra tool and CI step Olly doesn't need yet.
- **`TIMESTAMP` without time zone:** the JDBC driver converts using the JVM's default zone, so values silently shift when instances run in different zones.
