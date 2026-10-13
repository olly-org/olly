package io.github.olly_org.olly.realtime.pubsub;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

final class InMemoryPubSub implements PubSub {
  private final Map<String, Set<Consumer<byte[]>>> listeners = new ConcurrentHashMap<>();

  @Override
  public void publish(String channel, byte[] payload) {
    for (Consumer<byte[]> listener : listeners.getOrDefault(channel, Set.of())) {
      listener.accept(payload);
    }
  }

  @Override
  public Subscription subscribe(String channel, Consumer<byte[]> listener) {
    listeners.compute(
        channel,
        (_, existing) -> {
          Set<Consumer<byte[]>> set = existing != null ? existing : ConcurrentHashMap.newKeySet();
          set.add(listener);
          return set;
        });
    return () -> unsubscribe(channel, listener);
  }

  private void unsubscribe(String channel, Consumer<byte[]> listener) {
    listeners.computeIfPresent(
        channel,
        (_, existing) -> {
          existing.remove(listener);
          return existing.isEmpty() ? null : existing;
        });
  }
}
