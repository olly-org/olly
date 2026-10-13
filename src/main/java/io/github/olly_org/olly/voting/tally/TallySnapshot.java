package io.github.olly_org.olly.voting.tally;

import java.util.Map;
import java.util.UUID;

/**
 * Full counts for one poll at one moment. Clients keep the snapshot with the highest seq, so a lost
 * or reordered message is harmless.
 */
public record TallySnapshot(UUID pollId, long seq, Map<String, Long> counts) {
  public TallySnapshot {
    counts = Map.copyOf(counts);
  }
}
