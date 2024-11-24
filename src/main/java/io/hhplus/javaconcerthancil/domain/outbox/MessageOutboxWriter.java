package io.hhplus.javaconcerthancil.domain.outbox;

public interface MessageOutboxWriter {
    MessageOutbox save(MessageOutbox messageOutbox);
}
