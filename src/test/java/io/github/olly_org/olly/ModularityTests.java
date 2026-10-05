package io.github.olly_org.olly;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModularityTests {

  private final ApplicationModules modules = ApplicationModules.of(OllyApplication.class);

  @Test
  void verifiesModularStructure() {
    modules.verify();
  }

  @Test
  void writesDocumentation() {
    new Documenter(modules).writeDocumentation();
  }
}
