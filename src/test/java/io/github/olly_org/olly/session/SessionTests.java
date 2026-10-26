package io.github.olly_org.olly.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class SessionTests {

  private static Session openSession() {
    return Session.open(UUID.randomUUID(), UUID.randomUUID(), "ABC234", SessionKind.POLL);
  }

  @Test
  void opensWithTheJoinCodeLive() {
    Session session = openSession();

    assertThat(session.getState()).isEqualTo(Session.State.OPEN);
    assertThat(session.getJoinCode()).isEqualTo("ABC234");
    assertThat(session.getClosedAt()).isNull();
  }

  @Test
  void closingRecordsTheTime() {
    Session session = openSession();
    Instant now = Instant.parse("2026-10-26T12:00:00Z");

    session.close(now);

    assertThat(session.getState()).isEqualTo(Session.State.CLOSED);
    assertThat(session.getClosedAt()).isEqualTo(now);
    assertThat(session.getJoinCode()).isEqualTo("ABC234");
  }

  @Test
  void cannotCloseTwice() {
    Session session = openSession();
    session.close(Instant.now());

    assertThatThrownBy(() -> session.close(Instant.now()))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("already CLOSED");
  }

  @Test
  void onlyOpenSessionsCanClose() {
    assertThat(Session.State.OPEN.canTransitionTo(Session.State.CLOSED)).isTrue();
    assertThat(Session.State.CLOSED.canTransitionTo(Session.State.OPEN)).isFalse();
    assertThat(Session.State.CLOSED.canTransitionTo(Session.State.CLOSED)).isFalse();
    assertThat(Session.State.OPEN.canTransitionTo(Session.State.OPEN)).isFalse();
  }
}
