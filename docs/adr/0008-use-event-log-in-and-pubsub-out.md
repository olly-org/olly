# 0008. Send input through an event log and output through pub/sub

- Status: Accepted
- Date: 2026-10-13

## Context

A live poll can receive a million votes within seconds, and the result must be exact once the poll closes.
Live updates go to every connected screen and only need to be current, not complete.
These two flows need opposite guarantees: input must never be lost, and output must be fast and can be dropped.

## Decision

Votes are appended to a durable, ordered event log (`VoteLog`; Kafka in cluster mode).
The log keys votes by participant, so each voter's votes stay in order and one popular poll doesn't land on a single partition.
Live updates are published through pub/sub (`PubSub`; Redis sharded pub/sub in cluster mode), which delivers at most once.
Updates are full snapshots with a `seq` number, so a lost or reordered message is corrected by the next one, and `TallyView` never replaces a newer snapshot with an older one.

## Consequences

- Positive: Votes survive restarts and failovers in cluster mode, so final results can be exact.
- Positive: Fan-out stays fast and cheap, because pub/sub stores nothing.
- Positive: Clients need no retry or gap-filling logic; they keep the highest `seq`.
- Negative: Cluster mode runs two messaging systems.
- Negative: Snapshots are larger than deltas.

## Alternatives considered

- **Kafka for fan-out to clients:** durable but heavy for per-client delivery; Ably describes it as an anti-pattern.
- **Pub/sub for input:** at-most-once delivery would lose votes.
- **Keying votes by poll:** keeps a poll on one partition, which becomes a hot spot for large polls.
- **Deltas instead of snapshots:** smaller messages, but one lost delta corrupts the client's view until a full resync.

Twitch chat (edge, processing, pub/sub, edge) and Grafana Mimir's Kafka ingest path follow the same split.
