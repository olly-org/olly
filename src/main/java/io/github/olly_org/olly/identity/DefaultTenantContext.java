package io.github.olly_org.olly.identity;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
class DefaultTenantContext implements TenantContext {
  @Override
  public UUID currentTenantId() {
    return DEFAULT_TENANT_ID;
  }
}
