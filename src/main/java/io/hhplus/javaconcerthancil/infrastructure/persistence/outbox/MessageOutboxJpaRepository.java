package io.hhplus.javaconcerthancil.infrastructure.persistence.outbox;

import io.hhplus.javaconcerthancil.domain.outbox.EventType;
import io.hhplus.javaconcerthancil.domain.outbox.MessageOutbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageOutboxJpaRepository extends JpaRepository<MessageOutbox, Long> {
    List<MessageOutbox> findAllByTopicAndEventType(String topic, EventType eventType);
}
