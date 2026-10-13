package io.github.olly_org.olly.voting.ingest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class VoteLogContract {
  private static final UUID POLL = UUID.randomUUID();
  private VoteLog log;

  protected abstract VoteLog createVoteLog();

  @BeforeEach
  void setUp() {
    log = createVoteLog();
  }

  @Test
  void deliversAppendedVotes() {
    List<Vote> received = new CopyOnWriteArrayList<>();
    log.subscribe(received::add);
    Vote vote = vote(UUID.randomUUID(), "a");

    log.append(vote);

    await().untilAsserted(() -> assertThat(received).containsExactly(vote));
  }

  @Test
  void keepsEachParticipantsVotesInOrder() {
    List<Vote> received = new CopyOnWriteArrayList<>();
    log.subscribe(received::add);
    UUID participant = UUID.randomUUID();
    List<Vote> votes =
        IntStream.range(0, 100).mapToObj(i -> vote(participant, "choice-" + i)).toList();

    votes.forEach(log::append);

    await().untilAsserted(() -> assertThat(received).containsExactlyElementsOf(votes));
  }

  @Test
  void stopsDeliveringAfterClose() {
    List<Vote> closed = new CopyOnWriteArrayList<>();
    List<Vote> open = new CopyOnWriteArrayList<>();
    log.subscribe(closed::add).close();
    log.subscribe(open::add);
    Vote vote = vote(UUID.randomUUID(), "a");

    log.append(vote);

    await().untilAsserted(() -> assertThat(open).containsExactly(vote));
    assertThat(closed).isEmpty();
  }

  private static Vote vote(UUID participant, String choice) {
    return new Vote(UUID.randomUUID(), POLL, participant, choice, Instant.now());
  }
}
