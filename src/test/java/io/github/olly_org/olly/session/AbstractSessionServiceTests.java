package io.github.olly_org.olly.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.olly_org.olly.identity.TenantContext;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
abstract class AbstractSessionServiceTests {

  @Autowired SessionService sessions;

  @Autowired JdbcTemplate jdbc;

  @Test
  void createsAnOpenSessionWithAJoinCode() {
    Session session = sessions.create(SessionKind.POLL);

    assertThat(session.getState()).isEqualTo(Session.State.OPEN);
    assertThat(session.getKind()).isEqualTo(SessionKind.POLL);
    assertThat(session.getTenantId()).isEqualTo(TenantContext.DEFAULT_TENANT_ID);
    assertThat(session.getJoinCode()).matches("[A-HJ-NP-Z2-9]{6}");
    assertThat(session.getId().version()).isEqualTo(7);
    assertThat(session.getCreatedAt()).isNotNull().isEqualTo(session.getUpdatedAt());
  }

  @Test
  void storesTheKindAsItsPlainId() {
    Session session = sessions.create(SessionKind.QUIZ);

    String stored =
        jdbc.queryForObject(
            "SELECT kind FROM sessions WHERE id = ?", String.class, session.getId());
    assertThat(stored).isEqualTo("quiz");
  }

  @Test
  void findsByIdAndCaseInsensitiveJoinCode() {
    Session session = sessions.create(SessionKind.QUIZ);

    assertThat(sessions.find(session.getId()))
        .hasValueSatisfying(found -> assertThat(found.getKind()).isEqualTo(SessionKind.QUIZ));
    assertThat(sessions.findByJoinCode(session.getJoinCode().toLowerCase(Locale.ROOT)))
        .hasValueSatisfying(found -> assertThat(found.getId()).isEqualTo(session.getId()));
  }

  @Test
  void returnsEmptyForUnknownSession() {
    assertThat(sessions.find(UUID.randomUUID())).isEmpty();
    assertThat(sessions.findByJoinCode("ZZZZZZ")).isEmpty();
  }

  @Test
  void rejectsUnregisteredKinds() {
    assertThatThrownBy(() -> sessions.create(new SessionKind("acme.bingo")))
        .isInstanceOf(UnknownSessionKindException.class);
  }

  @Test
  void closesASessionOnlyOnce() {
    Session session = sessions.create(SessionKind.PROMPT);

    Session closed = sessions.close(session.getId());

    assertThat(closed.getState()).isEqualTo(Session.State.CLOSED);
    assertThat(closed.getClosedAt()).isNotNull();
    assertThat(closed.getUpdatedAt()).isAfterOrEqualTo(closed.getCreatedAt());
    assertThatThrownBy(() -> sessions.close(session.getId()))
        .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void closingReleasesTheJoinCode() {
    Session session = sessions.create(SessionKind.POLL);

    sessions.close(session.getId());

    assertThat(sessions.findByJoinCode(session.getJoinCode())).isEmpty();
    assertThat(sessions.find(session.getId()))
        .hasValueSatisfying(
            found -> assertThat(found.getJoinCode()).isEqualTo(session.getJoinCode()));
  }

  @Test
  void closingAnUnknownSessionFails() {
    assertThatThrownBy(() -> sessions.close(UUID.randomUUID()))
        .isInstanceOf(SessionNotFoundException.class);
  }

  @Test
  void doesNotSeeAnotherTenantsSessions() {
    UUID otherTenant = UUID.randomUUID();
    UUID otherSession = UUID.randomUUID();
    OffsetDateTime now = OffsetDateTime.now();
    jdbc.update(
        "INSERT INTO tenants (id, slug, name, created_at, updated_at) VALUES (?, ?, 'Other', ?, ?)",
        otherTenant,
        "other-" + otherTenant,
        now,
        now);
    jdbc.update(
        """
        INSERT INTO sessions
            (id, tenant_id, join_code, live_join_code, kind, state, version, created_at, updated_at)
        VALUES (?, ?, 'ZZZZZZ', 'ZZZZZZ', 'poll', 'OPEN', 0, ?, ?)
        """,
        otherSession,
        otherTenant,
        now,
        now);

    assertThat(sessions.find(otherSession)).isEmpty();
    assertThat(sessions.findByJoinCode("ZZZZZZ")).isEmpty();
  }
}
