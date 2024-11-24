package io.hhplus.javaconcerthancil.infrastructure.persistence.outbox;

import io.hhplus.javaconcerthancil.domain.outbox.MessageOutbox;
import io.hhplus.javaconcerthancil.domain.outbox.MessageOutboxWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MessageOutboxWriterImpl implements MessageOutboxWriter {

    private final MessageOutboxJpaRepository messageOutboxJpaRepository;

    @Override
    public MessageOutbox save(MessageOutbox messageOutbox) {
        return messageOutboxJpaRepository.save(messageOutbox);
    }

}
