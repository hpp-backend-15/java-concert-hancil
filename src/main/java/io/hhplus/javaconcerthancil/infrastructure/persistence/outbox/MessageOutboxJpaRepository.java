package io.hhplus.javaconcerthancil.infrastructure.persistence.outbox;

import io.hhplus.javaconcerthancil.domain.outbox.MessageOutbox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageOutboxJpaRepository extends JpaRepository<MessageOutbox, Long> {
}
