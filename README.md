# olly
Olly (Open Live Interactions)
Status: Early development, not usable yet.

## Configuration

With no configuration, Olly runs as a single standalone instance:
Every role runs in one process, pub/sub and the event log are in memory.

Set these as properties (`olly.*`) or as environment variables.

| Property | Environment variable | Default | Values |
|---|---|---|---|
| `olly.mode` | `OLLY_MODE` | `standalone` | `standalone`, `cluster` |
| `olly.node.roles` | `OLLY_NODE_ROLES` | `api,core,gateway` | any of `api`, `core`, `gateway` |
| `olly.pubsub.type` | `OLLY_PUBSUB_TYPE` | `in-memory` | `in-memory`, `redis` |
| `olly.event-log.type` | `OLLY_EVENTLOG_TYPE` | `in-memory` | `in-memory`, `kafka` |


### Modes

- **standalone**: one instance runs every role.
  Use this for local development and small self-hosted setups.
  In-memory pub/sub and event log are allowed.
- **cluster**: roles are split across instances.
  Pub/sub must be `redis` and the event log must be `kafka`.

Olly refuses to start if the settings don't fit together.
For example, a standalone instance without all three roles, or a cluster using in-memory pub/sub, fails at startup with a message naming the property to fix.

Cluster mode is not available yet.
The Redis and Kafka backends are planned for a later release.

### Roles

| Role | Responsibility |
|---|---|
| `api` | Handles HTTP requests (REST and GraphQL). |
| `gateway` | Holds client WebSocket connections and fans out live updates. |
| `core` | Runs game logic, owns room state, and processes each room's events in order. |


