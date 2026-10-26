package io.github.olly_org.olly.session;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class BuiltInSessionTypes {

  @Bean
  SessionType pollSessionType() {
    return new BuiltInSessionType(SessionKind.POLL, "Poll");
  }

  @Bean
  SessionType quizSessionType() {
    return new BuiltInSessionType(SessionKind.QUIZ, "Quiz");
  }

  @Bean
  SessionType promptSessionType() {
    return new BuiltInSessionType(SessionKind.PROMPT, "Prompt");
  }

  private record BuiltInSessionType(SessionKind kind, String displayName) implements SessionType {}
}
