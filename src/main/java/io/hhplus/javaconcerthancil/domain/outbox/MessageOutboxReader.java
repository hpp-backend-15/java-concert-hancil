package io.hhplus.javaconcerthancil.domain.outbox;

public interface MessageOutboxReader {

//    List<MessageOutbox> findAllBy(String topic, EventType eventType);
    MessageOutbox findById(Long id);
}
