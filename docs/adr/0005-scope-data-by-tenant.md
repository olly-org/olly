# 0005. Scope all data by tenant, starting with a built-in default tenant

- Status: Accepted
- Date: 2026-10-11

## Context

Most Olly deployments have a single owner.
We still want to allow for more than one.
Adding an owner column after the fact means a migration on every table and a change to every query; GitLab's multi-year Organizations effort shows how costly that retrofit is.

## Decision

Tenants share tables, and every owned table has a `tenant_id` column with a foreign key to `tenants` (the pool model).
A migration creates a built-in default tenant with a fixed id, so every deployment starts with one owner, and Flyway's lock makes the insert safe when instances start together.
The default tenant is permanent: it is the fallback tenant after multi-tenancy arrives, and it can be renamed but not deleted.
Code gets the current tenant from a single `TenantContext` rather than passing it through every call.
Once authentication exists, `TenantContext` takes the tenant only from authenticated credentials (an API key or a verified JWT), never from client input such as headers or parameters.
Tenant ids are identifiers, not secrets; a well-known default id is safe because access always depends on credentials.

## Consequences

- Positive: Hosting many tenants later needs no data migration.
- Positive: Tenant isolation can be tested from the first aggregate onward.
- Neutral: The default tenant id is public, like Kubernetes' `default` namespace or Pulsar's `public` tenant.
- Negative: Every owned table carries one extra column, and every query one extra filter.

## Alternatives considered

- **One deployment per owner (silo model, like Discourse multisite):** simplest code, but no shared hosting and more infrastructure per owner.
- **One database schema per tenant:** strong isolation, but migrations multiply with every tenant.
- **Tenant ids without a tenants table (like Grafana Mimir):** smallest schema, but the database can't guarantee a session's tenant exists.
- **Creating the default tenant in code at startup:** configurable, but needs race handling across instances that Flyway's lock already provides.
- **A randomly generated default tenant id:** no security gain, and code would have to look the tenant up instead of using a constant.
