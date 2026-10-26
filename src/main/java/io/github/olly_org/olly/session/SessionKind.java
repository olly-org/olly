package io.github.olly_org.olly.session;

import java.util.regex.Pattern;

public record SessionKind(String id) {
  private static final Pattern FORMAT = Pattern.compile("[a-z][a-z0-9-]*(\\.[a-z][a-z0-9-]*)*");
  private static final int MAX_LENGTH = 64;

  public static final SessionKind POLL = new SessionKind("poll");
  public static final SessionKind QUIZ = new SessionKind("quiz");
  public static final SessionKind PROMPT = new SessionKind("prompt");

  public SessionKind {
    if (id == null || id.length() > MAX_LENGTH || !FORMAT.matcher(id).matches()) {
      throw new IllegalArgumentException("Invalid session kind: " + id);
    }
  }

  @Override
  public String toString() {
    return id;
  }
}
