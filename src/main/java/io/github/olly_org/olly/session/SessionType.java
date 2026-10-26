package io.github.olly_org.olly.session;

public interface SessionType {
  SessionKind kind();

  String displayName();
}
