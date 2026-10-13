package io.github.olly_org.olly.voting.ingest;

import io.github.olly_org.olly.OllyProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class VoteLogConfiguration {
  @Bean
  VoteLog voteLog(OllyProperties properties) {
    return switch (properties.eventLog().type()) {
      case IN_MEMORY -> new InMemoryVoteLog();
      case KAFKA ->
          throw new IllegalStateException("olly.event-log.type=kafka is not yet available.");
    };
  }
}
