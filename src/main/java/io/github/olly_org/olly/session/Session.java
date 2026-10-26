package io.github.olly_org.olly.session;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Table("sessions")
public class Session {
  @Id private final UUID id;
  private final UUID tenantId;
  private final String joinCode;

  /** The join code while the session is open, so guests can find it; null once closed. */
  private String liveJoinCode;

  private final SessionKind kind;
  private State state;
  @Version private Integer version;
  @CreatedDate private Instant createdAt;
  @LastModifiedDate private Instant updatedAt;
  private Instant closedAt;

  @PersistenceCreator
  Session(
      UUID id,
      UUID tenantId,
      String joinCode,
      String liveJoinCode,
      SessionKind kind,
      State state,
      Integer version,
      Instant createdAt,
      Instant updatedAt,
      Instant closedAt) {
    this.id = id;
    this.tenantId = tenantId;
    this.joinCode = joinCode;
    this.liveJoinCode = liveJoinCode;
    this.kind = kind;
    this.state = state;
    this.version = version;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.closedAt = closedAt;
  }

  static Session open(UUID id, UUID tenantId, String joinCode, SessionKind kind) {
    return new Session(id, tenantId, joinCode, joinCode, kind, State.OPEN, null, null, null, null);
  }

  void close(Instant now) {
    if (!state.canTransitionTo(State.CLOSED)) {
      throw new IllegalStateException("Session " + id + " is already " + state + ".");
    }
    state = State.CLOSED;
    liveJoinCode = null;
    closedAt = now;
  }

  public UUID getId() {
    return id;
  }

  public UUID getTenantId() {
    return tenantId;
  }

  public String getJoinCode() {
    return joinCode;
  }

  public SessionKind getKind() {
    return kind;
  }

  public State getState() {
    return state;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public Instant getClosedAt() {
    return closedAt;
  }

  public enum State {
    OPEN,
    CLOSED;

    boolean canTransitionTo(State next) {
      return this == OPEN && next == CLOSED;
    }
  }
}
