package io.github.olly_org.olly.voting.tally;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

final class InMemoryTallyView implements TallyView {
  private final Map<UUID, TallySnapshot> latest = new ConcurrentHashMap<>();

  @Override
  public void put(TallySnapshot snapshot) {
    latest.merge(
        snapshot.pollId(),
        snapshot,
        (current, incoming) -> incoming.seq() > current.seq() ? incoming : current);
  }

  @Override
  public Optional<TallySnapshot> latest(UUID pollId) {
    return Optional.ofNullable(latest.get(pollId));
  }
}
