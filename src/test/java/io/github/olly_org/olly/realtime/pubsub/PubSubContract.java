package io.github.olly_org.olly.realtime.pubsub;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class PubSubContract {
  private PubSub pubSub;

  protected abstract PubSub createPubSub();

  @BeforeEach
  void setUp() {
    pubSub = createPubSub();
  }

  @Test
  void deliversToSubscribersOfTheChannel() {
    List<String> received = new CopyOnWriteArrayList<>();
    pubSub.subscribe("poll.1", bytes -> received.add(new String(bytes, UTF_8)));

    pubSub.publish("poll.1", "hello".getBytes(UTF_8));

    await().untilAsserted(() -> assertThat(received).containsExactly("hello"));
  }

  @Test
  void deliversToEverySubscriber() {
    List<String> first = new CopyOnWriteArrayList<>();
    List<String> second = new CopyOnWriteArrayList<>();
    pubSub.subscribe("poll.1", bytes -> first.add(new String(bytes, UTF_8)));
    pubSub.subscribe("poll.1", bytes -> second.add(new String(bytes, UTF_8)));

    pubSub.publish("poll.1", "hello".getBytes(UTF_8));

    await()
        .untilAsserted(
            () -> {
              assertThat(first).containsExactly("hello");
              assertThat(second).containsExactly("hello");
            });
  }

  @Test
  void doesNotDeliverToOtherChannels() {
    List<String> received = new CopyOnWriteArrayList<>();
    pubSub.subscribe("poll.2", bytes -> received.add(new String(bytes, UTF_8)));

    pubSub.publish("poll.1", "other".getBytes(UTF_8));
    pubSub.publish("poll.2", "marker".getBytes(UTF_8));

    await().untilAsserted(() -> assertThat(received).containsExactly("marker"));
  }

  @Test
  void stopsDeliveringAfterClose() {
    List<String> closed = new CopyOnWriteArrayList<>();
    List<String> open = new CopyOnWriteArrayList<>();
    pubSub.subscribe("poll.1", bytes -> closed.add(new String(bytes, UTF_8))).close();
    pubSub.subscribe("poll.1", bytes -> open.add(new String(bytes, UTF_8)));

    pubSub.publish("poll.1", "hello".getBytes(UTF_8));

    await().untilAsserted(() -> assertThat(open).containsExactly("hello"));
    assertThat(closed).isEmpty();
  }
}
