package io.github.olly_org.olly.voting.tally;

import java.util.Optional;
import java.util.UUID;

/** Latest tally snapshot per poll. */
public interface TallyView {
  /** Stores the snapshot unless a newer one (higher seq) is already stored for its poll. */
  void put(TallySnapshot snapshot);

  /** Returns the newest stored snapshot for the poll, or empty if none has been stored. */
  Optional<TallySnapshot> latest(UUID pollId);
}
