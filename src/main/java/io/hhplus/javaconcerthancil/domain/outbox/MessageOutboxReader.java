package io.hhplus.javaconcerthancil.domain.outbox;

import java.util.List;

public interface MessageOutboxReader {

    List<MessageOutbox> findAllBy(String topic, EventType eventType);
    MessageOutbox findById(Long id);
}
