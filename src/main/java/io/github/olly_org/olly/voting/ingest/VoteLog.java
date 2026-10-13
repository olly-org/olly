package io.github.olly_org.olly.voting.ingest;

import java.util.function.Consumer;

/**
 * Durable log of accepted votes, consumed by tally. Each participant's votes are delivered in the
 * order they were appended.
 */
public interface VoteLog {
  /** Appends the vote; once this returns, the vote will be delivered to subscribers. */
  void append(Vote vote);

  /** Delivers votes appended from now on to the consumer until the subscription is closed. */
  Subscription subscribe(Consumer<Vote> consumer);

  /** Stops delivery to one consumer. */
  interface Subscription extends AutoCloseable {
    @Override
    void close();
  }
}
