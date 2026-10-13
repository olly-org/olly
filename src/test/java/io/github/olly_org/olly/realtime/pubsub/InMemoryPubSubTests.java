package io.github.olly_org.olly.realtime.pubsub;

class InMemoryPubSubTests extends PubSubContract {

  @Override
  protected PubSub createPubSub() {
    return new InMemoryPubSub();
  }
}
