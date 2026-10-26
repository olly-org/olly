package io.github.olly_org.olly.session;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class SessionTypes {
  private final Map<SessionKind, SessionType> byKind;

  SessionTypes(List<SessionType> types) {
    this.byKind =
        types.stream()
            .collect(
                Collectors.toUnmodifiableMap(
                    SessionType::kind,
                    Function.identity(),
                    (first, second) -> {
                      throw new IllegalStateException(
                          "Two session types registered for kind '" + first.kind() + "'.");
                    }));
  }

  public SessionType require(SessionKind kind) {
    SessionType type = byKind.get(kind);
    if (type == null) {
      throw new UnknownSessionKindException(kind, byKind.keySet());
    }
    return type;
  }

  public Collection<SessionType> all() {
    return byKind.values();
  }
}
