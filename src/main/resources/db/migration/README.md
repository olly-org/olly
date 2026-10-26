# Database migrations

Flyway applies every `V<n>__*.sql` file in this folder, in version order, on startup.

## Layout

All migrations live in this one folder, so the file listing is the order they run in.
Each file name says which application module owns the change.

## Naming

- Files: `V<n>__<module>_<verb>_<object>.sql`, for example `V3__session_create_sessions.sql`.
- Tables: plural, snake_case.
- Columns: snake_case; timestamps end in `_at`; lifecycle columns are named `state`.
- Timestamps use `TIMESTAMP WITH TIME ZONE` and hold UTC instants.
- Every table has `created_at`; tables whose rows change also have `updated_at`, set by the application, not by triggers.
- Constraints: `pk_<table>`, `fk_<table>_<column>`, `uq_<table>_<columns>`, `ck_<table>_<column>`, `ix_<table>_<columns>`.
- Every table except `tenants` has a `tenant_id`.
- Values that plugins can extend (such as `kind`) have no `CHECK` constraint; platform-owned categories (such as `state`) do.
- Don't use SQL reserved words (such as `user`, `order`, `value`, `key`) as table or column names; Olly's SQL leaves identifiers unquoted.

## Rules

- SQL must run unchanged on H2 and PostgreSQL; `AbstractMigrationTests` checks both.
- Never edit or delete a migration after it is merged; write a new one.
- Change schemas in two releases: first add (new columns nullable, new tables), then remove what the previous release still used.
- Create a table in the same pull request as the code that uses it.
