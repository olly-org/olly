package io.github.olly_org.olly.voting.ingest;

import java.time.Instant;
import java.util.UUID;

public record Vote(
    UUID voteId, UUID pollId, UUID participantId, String choice, Instant receivedAt) {}
