package io.github.olly_org.olly;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import java.util.UUID;

public final class Ids {
  private static final TimeBasedEpochGenerator GENERATOR = Generators.timeBasedEpochGenerator();

  private Ids() {}

  public static UUID newId() {
    return GENERATOR.generate();
  }
}
