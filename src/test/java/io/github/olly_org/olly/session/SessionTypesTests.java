package io.github.olly_org.olly.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;

class SessionTypesTests {

  private static SessionType type(String id) {
    SessionKind kind = new SessionKind(id);
    return new SessionType() {
      @Override
      public SessionKind kind() {
        return kind;
      }

      @Override
      public String displayName() {
        return id;
      }
    };
  }

  @Test
  void findsRegisteredTypes() {
    SessionType poll = type("poll");
    SessionTypes types = new SessionTypes(List.of(poll, type("acme.bingo")));

    assertThat(types.require(SessionKind.POLL)).isSameAs(poll);
    assertThat(types.all()).hasSize(2);
  }

  @Test
  void rejectsUnknownKindsAndListsTheAvailableOnes() {
    SessionTypes types = new SessionTypes(List.of(type("quiz"), type("poll")));

    assertThatThrownBy(() -> types.require(new SessionKind("acme.bingo")))
        .isInstanceOf(UnknownSessionKindException.class)
        .hasMessage("Unknown session kind 'acme.bingo'. Available: poll, quiz.");
  }

  @Test
  void refusesTwoTypesForTheSameKind() {
    assertThatThrownBy(() -> new SessionTypes(List.of(type("poll"), type("poll"))))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Two session types registered for kind 'poll'.");
  }
}
