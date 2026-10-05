package io.github.olly_org.olly;

import java.util.EnumSet;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Olly deployment settings.
 *
 * @param mode How this deployment runs. Standalone runs every role in a single instance and allows
 *     in-memory pub/sub and event log. Cluster splits roles across instances and requires redis and
 *     kafka.
 */
@ConfigurationProperties("olly")
public record OllyProperties(
    @DefaultValue("standalone") Mode mode,
    @DefaultValue Node node,
    @DefaultValue PubSub pubsub,
    @DefaultValue EventLog eventLog) {

  public OllyProperties {
    switch (mode) {
      case STANDALONE -> {
        if (!node.runsAllRoles()) {
          throw new IllegalArgumentException(
              "olly.node.roles must include api, core and gateway when olly.mode=standalone."
                  + " "
                  + "Set olly.mode=cluster to split roles across instances.");
        }
      }
      case CLUSTER -> {
        if (pubsub.type() == PubSub.Type.IN_MEMORY) {
          throw new IllegalArgumentException(
              "olly.pubsub.type=in-memory is not compatible with olly.mode=cluster. Use redis.");
        }
        if (eventLog.type() == EventLog.Type.IN_MEMORY) {
          throw new IllegalArgumentException(
              "olly.event-log.type=in-memory is not compatible with olly.mode=cluster. Use kafka.");
        }
      }
    }
  }

  public enum Mode {
    STANDALONE,
    CLUSTER
  }

  /**
   * This instance's role in an Olly deployment.
   *
   * @param roles Comma-separated list of roles this instance runs.
   */
  public record Node(@DefaultValue({"api", "core", "gateway"}) Set<Role> roles) {
    public Node {
      if (roles.isEmpty()) {
        throw new IllegalArgumentException("olly.node.roles must not be empty.");
      }
      roles = Set.copyOf(roles);
    }

    public boolean runsAllRoles() {
      return roles.containsAll(EnumSet.allOf(Role.class));
    }

    public enum Role {
      API,
      CORE,
      GATEWAY
    }
  }

  /**
   * Fan-out of live updates from core to gateways.
   *
   * @param type Pub/sub implementation. In-memory reaches only this instance; use redis when
   *     running multiple instances.
   */
  public record PubSub(@DefaultValue("in-memory") Type type) {
    public enum Type {
      IN_MEMORY,
      REDIS
    }
  }

  /**
   * Durable, ordered stream of client input consumed by core.
   *
   * @param type Event log implementation. In-memory reaches only this instance and is lost on
   *     restart; use kafka when running multiple instances.
   */
  public record EventLog(@DefaultValue("in-memory") Type type) {
    public enum Type {
      IN_MEMORY,
      KAFKA
    }
  }
}
