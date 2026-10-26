package io.github.olly_org.olly;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
abstract class AbstractMigrationTests {

  private static final UUID DEFAULT_TENANT_ID =
      UUID.fromString("00000000-0000-0000-0000-000000000001");

  @Autowired Flyway flyway;

  @Autowired JdbcTemplate jdbc;

  @Test
  void appliesEveryMigration() {
    assertThat(flyway.info().pending()).isEmpty();
    assertThat(flyway.info().current().getVersion().getVersion()).isEqualTo("3");
  }

  @Test
  void insertsDefaultTenant() {
    Integer count =
        jdbc.queryForObject(
            "SELECT count(*) FROM tenants WHERE id = ? AND slug = 'default'",
            Integer.class,
            DEFAULT_TENANT_ID);
    assertThat(count).isEqualTo(1);
  }

  @Test
  @Transactional
  void rejectsDuplicateJoinCodeAmongOpenSessions() {
    insertSession("ABC234", "poll", "OPEN");

    assertThatThrownBy(() -> insertSession("ABC234", "poll", "OPEN"))
        .isInstanceOf(DuplicateKeyException.class);
  }

  @Test
  @Transactional
  void reusesJoinCodesOfClosedSessions() {
    insertSession("ABC234", "poll", "CLOSED");
    insertSession("ABC234", "poll", "CLOSED");
    insertSession("ABC234", "poll", "OPEN");

    Integer count =
        jdbc.queryForObject(
            "SELECT count(*) FROM sessions WHERE join_code = 'ABC234'", Integer.class);
    assertThat(count).isEqualTo(3);
  }

  @Test
  @Transactional
  void rejectsOpenSessionWithoutLiveJoinCode() {
    assertThatThrownBy(() -> insertSession("ABC234", null, "poll", "OPEN"))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  @Transactional
  void rejectsClosedSessionHoldingLiveJoinCode() {
    assertThatThrownBy(() -> insertSession("ABC234", "ABC234", "poll", "CLOSED"))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  @Transactional
  void acceptsPluginKindsWithoutMigration() {
    insertSession("BNG234", "acme.bingo", "OPEN");

    Integer count =
        jdbc.queryForObject(
            "SELECT count(*) FROM sessions WHERE kind = 'acme.bingo'", Integer.class);
    assertThat(count).isEqualTo(1);
  }

  @Test
  @Transactional
  void rejectsUnknownLifecycleState() {
    assertThatThrownBy(() -> insertSession("STA234", "poll", "PAUSED"))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  private void insertSession(String joinCode, String kind, String state) {
    insertSession(joinCode, "OPEN".equals(state) ? joinCode : null, kind, state);
  }

  private void insertSession(String joinCode, String liveJoinCode, String kind, String state) {
    OffsetDateTime now = OffsetDateTime.now();
    jdbc.update(
        """
        INSERT INTO sessions
            (id, tenant_id, join_code, live_join_code, kind, state, version, created_at, updated_at)
        VALUES (?, ?, ?, ?, ?, ?, 0, ?, ?)
        """,
        UUID.randomUUID(),
        DEFAULT_TENANT_ID,
        joinCode,
        liveJoinCode,
        kind,
        state,
        now,
        now);
  }
}
