package io.github.olly_org.olly.session;

import java.util.UUID;

public class SessionNotFoundException extends RuntimeException {

  public SessionNotFoundException(UUID id) {
    super("Session " + id + " was not found.");
  }
}
