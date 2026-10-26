package io.github.olly_org.olly.session;

import java.util.Collection;
import java.util.stream.Collectors;

public class UnknownSessionKindException extends RuntimeException {

  public UnknownSessionKindException(SessionKind kind, Collection<SessionKind> available) {
    super(
        "Unknown session kind '"
            + kind
            + "'. Available: "
            + available.stream().map(SessionKind::id).sorted().collect(Collectors.joining(", "))
            + ".");
  }
}
