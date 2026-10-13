package io.github.olly_org.olly.realtime.pubsub;

import java.util.function.Consumer;

/**
 * Fan-out of live updates to every instance. Delivery is at most once: a listener that is slow,
 * disconnected or not yet subscribed may miss a message, so payloads must be safe to lose.
 */
public interface PubSub {
  /** Sends the payload to every current subscriber of the channel, on any instance. */
  void publish(String channel, byte[] payload);

  /** Delivers future messages on the channel to the listener until the subscription is closed. */
  Subscription subscribe(String channel, Consumer<byte[]> listener);

  /** Stops delivery to one listener. */
  interface Subscription extends AutoCloseable {
    @Override
    void close();
  }
}
