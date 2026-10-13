package io.github.olly_org.olly.realtime.pubsub;

import io.github.olly_org.olly.OllyProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class PubSubConfiguration {

  @Bean
  PubSub pubSub(OllyProperties ollyProperties) {
    return switch (ollyProperties.pubsub().type()) {
      case IN_MEMORY -> new InMemoryPubSub();
      case REDIS -> throw new IllegalStateException("olly.pubsub.type=redis is not yet available.");
    };
  }
}
