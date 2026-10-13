package io.github.olly_org.olly.voting.ingest;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

final class InMemoryVoteLog implements VoteLog {
  private final Set<Consumer<Vote>> consumers = new CopyOnWriteArraySet<>();

  @Override
  public void append(Vote vote) {
    for (Consumer<Vote> consumer : consumers) {
      consumer.accept(vote);
    }
  }

  @Override
  public Subscription subscribe(Consumer<Vote> consumer) {
    consumers.add(consumer);
    return () -> consumers.remove(consumer);
  }
}
