package io.github.olly_org.olly.identity;

import java.util.UUID;

public interface TenantContext {
  UUID DEFAULT_TENANT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

  UUID currentTenantId();
}
