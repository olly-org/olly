package io.github.olly_org.olly.voting.tally;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class TallyViewConfiguration {
  @Bean
  TallyView tallyView() {
    return new InMemoryTallyView();
  }
}
