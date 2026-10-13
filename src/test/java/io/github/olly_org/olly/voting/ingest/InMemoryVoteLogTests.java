package io.github.olly_org.olly.voting.ingest;

class InMemoryVoteLogTests extends VoteLogContract {

  @Override
  protected VoteLog createVoteLog() {
    return new InMemoryVoteLog();
  }
}
