package io.github.olly_org.olly.voting.tally;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class TallyViewContract {

  private TallyView view;

  protected abstract TallyView createTallyView();

  @BeforeEach
  void setUp() {
    view = createTallyView();
  }

  @Test
  void isEmptyForUnknownPoll() {
    assertThat(view.latest(UUID.randomUUID())).isEmpty();
  }

  @Test
  void returnsTheNewestSnapshot() {
    UUID poll = UUID.randomUUID();
    view.put(new TallySnapshot(poll, 1, Map.of("a", 1L)));
    view.put(new TallySnapshot(poll, 2, Map.of("a", 2L)));

    assertThat(view.latest(poll)).hasValueSatisfying(s -> assertThat(s.seq()).isEqualTo(2));
  }

  @Test
  void ignoresSnapshotsThatArriveLate() {
    UUID poll = UUID.randomUUID();
    view.put(new TallySnapshot(poll, 5, Map.of("a", 5L)));
    view.put(new TallySnapshot(poll, 4, Map.of("a", 4L)));

    assertThat(view.latest(poll)).hasValueSatisfying(s -> assertThat(s.seq()).isEqualTo(5));
  }
}
