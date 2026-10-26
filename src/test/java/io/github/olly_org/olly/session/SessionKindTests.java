package io.github.olly_org.olly.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SessionKindTests {

  @ParameterizedTest
  @ValueSource(strings = {"poll", "quiz", "prompt", "word-cloud", "acme.bingo", "acme.party-2"})
  void acceptsBuiltInAndNamespacedKinds(String id) {
    assertThat(new SessionKind(id).id()).isEqualTo(id);
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "Poll", "2poll", "poll.", ".poll", "acme..bingo", "acme bingo"})
  void rejectsMalformedKinds(String id) {
    assertThatThrownBy(() -> new SessionKind(id)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void rejectsNull() {
    assertThatThrownBy(() -> new SessionKind(null)).isInstanceOf(IllegalArgumentException.class);
  }

  @ParameterizedTest
  @ValueSource(ints = {65, 200})
  void rejectsKindsLongerThanTheColumn(int length) {
    assertThatThrownBy(() -> new SessionKind("a".repeat(length)))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void acceptsKindsAtTheColumnLimit() {
    assertThat(new SessionKind("a".repeat(64)).id()).hasSize(64);
  }

  @Test
  void printsAsItsId() {
    assertThat(SessionKind.POLL).hasToString("poll");
  }
}
