package io.github.olly_org.olly.voting.tally;

class InMemoryTallyViewTests extends TallyViewContract {

  @Override
  protected TallyView createTallyView() {
    return new InMemoryTallyView();
  }
}
