package io.github.olly_org.olly.session;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.ListCrudRepository;

interface SessionRepository extends ListCrudRepository<Session, UUID> {
  Optional<Session> findByTenantIdAndId(UUID tenantId, UUID id);

  /** Finds the open session using this join code; closed sessions have released theirs. */
  Optional<Session> findByTenantIdAndLiveJoinCode(UUID tenantId, String joinCode);
}
