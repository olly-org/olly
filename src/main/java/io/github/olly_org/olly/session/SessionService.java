package io.github.olly_org.olly.session;

import io.github.olly_org.olly.Ids;
import io.github.olly_org.olly.identity.TenantContext;
import java.time.Clock;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
public class SessionService {
  private static final int MAX_JOIN_CODE_ATTEMPTS = 3;

  private final SessionRepository sessions;
  private final SessionTypes types;
  private final JoinCodes joinCodes;
  private final TenantContext tenants;
  private final Clock clock;

  SessionService(
      SessionRepository sessions,
      SessionTypes types,
      JoinCodes joinCodes,
      TenantContext tenants,
      Clock clock) {
    this.sessions = sessions;
    this.types = types;
    this.joinCodes = joinCodes;
    this.tenants = tenants;
    this.clock = clock;
  }

  public Session create(SessionKind kind) {
    types.require(kind);
    UUID tenantId = tenants.currentTenantId();
    for (int attempt = 1; ; attempt++) {
      Session session = Session.open(Ids.newId(), tenantId, joinCodes.next(), kind);
      try {
        return sessions.save(session);
      } catch (RuntimeException e) {
        if (!isDuplicateKey(e) || attempt == MAX_JOIN_CODE_ATTEMPTS) {
          throw e;
        }
      }
    }
  }

  public Optional<Session> find(UUID id) {
    return sessions.findByTenantIdAndId(tenants.currentTenantId(), id);
  }

  public Optional<Session> findByJoinCode(String joinCode) {
    return sessions.findByTenantIdAndLiveJoinCode(
        tenants.currentTenantId(), joinCode.toUpperCase(Locale.ROOT));
  }

  public Session close(UUID id) {
    Session session = find(id).orElseThrow(() -> new SessionNotFoundException(id));
    session.close(clock.instant());
    return sessions.save(session);
  }

  private static boolean isDuplicateKey(Throwable e) {
    for (Throwable cause = e; cause != null; cause = cause.getCause()) {
      if (cause instanceof DuplicateKeyException) {
        return true;
      }
    }
    return false;
  }
}
