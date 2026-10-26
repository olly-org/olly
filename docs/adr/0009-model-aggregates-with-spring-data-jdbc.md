# 0009. Model aggregates with Spring Data JDBC

- Status: Accepted
- Date: 2026-10-26

## Context

Olly's domain is a set of explicit aggregates, starting with sessions.
SQL must behave the same on H2 and PostgreSQL, and every query must be scoped by tenant.
Several instances can create sessions at once, so uniqueness can't rely on checking before inserting.
Join codes are short so people can type them, which means they must be reusable once a session closes.

## Decision

Aggregates are mapped directly with Spring Data JDBC; there is no separate entity class and no JPA.
An aggregate changes state only through its own methods, and its repository is package-private to its module.
Identifiers are application-generated UUIDv7 values created through `Ids`, and a null `@Version` marks a new aggregate.
`created_at` and `updated_at` are filled by Spring Data auditing from one shared `Clock`, truncated to microseconds to match database precision.
Each module registers converters for its own value types through `JdbcConverterProvider`.
Identifiers are not quoted, so H2 and PostgreSQL each apply their own case rules to names created by the migrations.
Uniqueness is enforced by database constraints, and the service retries when a constraint is hit.
A session keeps its `join_code` for history, and holds it in `live_join_code` only while open; uniqueness applies to `live_join_code`, so closing a session releases its code.
Aggregate tests run against both H2 and PostgreSQL.

## Consequences

- Positive: No lazy loading, proxies or dirty checking; the SQL that runs is easy to predict.
- Positive: Other modules can read aggregates but can't change or save them.
- Positive: Timestamps returned by the API match what is stored.
- Positive: Join codes are recycled without losing which code a past session used.
- Negative: More hand-written queries than with JPA.
- Negative: Table and column names can't be SQL reserved words, because they are unquoted.

## Alternatives considered

- **JPA/Hibernate:** hidden flush timing, lazy-initialisation errors and N+1 queries.
- **Hibernate `@UuidGenerator(style = VERSION_7)`:** only works with JPA, and assigns the id at persist time instead of when the aggregate is created.
- **JDK `UUID.ofEpochMillis`:** the standard choice, but it needs Java 26 or later; `Ids` hides the library so the switch is one line.
- **Random UUIDv4 identifiers:** scattered index writes.
- **Checking join codes before inserting:** races between instances.
- **A partial unique index on open sessions:** not portable to H2.
- **Quoted identifiers:** would require names to match each database's case rules exactly.
