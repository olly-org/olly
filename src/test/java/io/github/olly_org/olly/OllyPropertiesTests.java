package io.github.olly_org.olly;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.olly_org.olly.OllyProperties.EventLog;
import io.github.olly_org.olly.OllyProperties.Mode;
import io.github.olly_org.olly.OllyProperties.Node.Role;
import io.github.olly_org.olly.OllyProperties.PubSub;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.assertj.AssertableApplicationContext;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.Configuration;

class OllyPropertiesTests {
  private final ApplicationContextRunner runner =
      new ApplicationContextRunner().withUserConfiguration(Config.class);

  @Test
  void defaultsToStandaloneWithEveryRoleInMemory() {
    runner.run(
        context -> {
          OllyProperties properties = context.getBean(OllyProperties.class);
          assertThat(properties.mode()).isEqualTo(Mode.STANDALONE);
          assertThat(properties.node().roles())
              .containsExactlyInAnyOrder(Role.API, Role.CORE, Role.GATEWAY);
          assertThat(properties.node().runsAllRoles()).isTrue();
          assertThat(properties.pubsub().type()).isEqualTo(PubSub.Type.IN_MEMORY);
          assertThat(properties.eventLog().type()).isEqualTo(EventLog.Type.IN_MEMORY);
        });
  }

  @Test
  void bindsKebabCaseValues() {
    runner
        .withPropertyValues("olly.pubsub.type=redis", "olly.event-log.type=in-memory")
        .run(
            context -> {
              OllyProperties properties = context.getBean(OllyProperties.class);
              assertThat(properties.pubsub().type()).isEqualTo(PubSub.Type.REDIS);
              assertThat(properties.eventLog().type()).isEqualTo(EventLog.Type.IN_MEMORY);
            });
  }

  @Test
  void standaloneRejectsSplitRoles() {
    runner
        .withPropertyValues("olly.node.roles=gateway")
        .run(context -> assertFailsMentioning(context, "olly.node.roles"));
  }

  @Test
  void clusterRejectsInMemoryPubSub() {
    runner
        .withPropertyValues("olly.mode=cluster")
        .run(context -> assertFailsMentioning(context, "olly.pubsub.type"));
  }

  @Test
  void clusterRejectsInMemoryEventLog() {
    runner
        .withPropertyValues("olly.mode=cluster", "olly.pubsub.type=redis")
        .run(context -> assertFailsMentioning(context, "olly.event-log.type"));
  }

  @Test
  void clusterAcceptsSplitRolesWithExternalBackends() {
    runner
        .withPropertyValues(
            "olly.mode=cluster",
            "olly.node.roles=gateway",
            "olly.pubsub.type=redis",
            "olly.event-log.type=kafka")
        .run(
            context -> {
              OllyProperties properties = context.getBean(OllyProperties.class);
              assertThat(properties.mode()).isEqualTo(Mode.CLUSTER);
              assertThat(properties.node().roles()).containsExactly(Role.GATEWAY);
              assertThat(properties.node().runsAllRoles()).isFalse();
            });
  }

  @Test
  void rejectsEmptyRoles() {
    runner
        .withPropertyValues("olly.node.roles=")
        .run(context -> assertFailsMentioning(context, "olly.node.roles"));
  }

  @Test
  void rejectsUnknownValues() {
    runner
        .withPropertyValues("olly.pubsub.type=nope")
        .run(context -> assertThat(context).hasFailed());
  }

  @Test
  void bindsFromEnvironmentVariables() {
    runner
        .withInitializer(
            context ->
                TestPropertyValues.of(
                        "OLLY_MODE=cluster",
                        "OLLY_NODE_ROLES=api,core",
                        "OLLY_PUBSUB_TYPE=redis",
                        "OLLY_EVENTLOG_TYPE=kafka")
                    .applyTo(context.getEnvironment(), TestPropertyValues.Type.SYSTEM_ENVIRONMENT))
        .run(
            context -> {
              OllyProperties properties = context.getBean(OllyProperties.class);
              assertThat(properties.mode()).isEqualTo(Mode.CLUSTER);
              assertThat(properties.node().roles()).containsExactlyInAnyOrder(Role.API, Role.CORE);
              assertThat(properties.pubsub().type()).isEqualTo(PubSub.Type.REDIS);
              assertThat(properties.eventLog().type()).isEqualTo(EventLog.Type.KAFKA);
            });
  }

  private static void assertFailsMentioning(AssertableApplicationContext context, String property) {
    assertThat(context).hasFailed();
    assertThat(context.getStartupFailure()).rootCause().hasMessageContaining(property);
  }

  @Configuration(proxyBeanMethods = false)
  @EnableConfigurationProperties(OllyProperties.class)
  static class Config {}
}
