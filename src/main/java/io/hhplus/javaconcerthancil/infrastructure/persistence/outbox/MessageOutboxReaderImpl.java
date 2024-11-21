package io.hhplus.javaconcerthancil.infrastructure.persistence.outbox;

import io.hhplus.javaconcerthancil.domain.outbox.EventType;
import io.hhplus.javaconcerthancil.domain.outbox.MessageOutbox;
import io.hhplus.javaconcerthancil.domain.outbox.MessageOutboxReader;
import io.hhplus.javaconcerthancil.interfaces.api.common.ApiException;
import io.hhplus.javaconcerthancil.interfaces.api.common.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MessageOutboxReaderImpl implements MessageOutboxReader {

    private final MessageOutboxJpaRepository messageOutboxJpaRepository;


    @Override
    public List<MessageOutbox> findAllBy(String topic, EventType eventType) {
        return messageOutboxJpaRepository.findAllByTopicAndEventType(topic, eventType);
    }

    @Override
    public MessageOutbox findById(Long id) {
        return messageOutboxJpaRepository.findById(id).orElseThrow(
                () -> new ApiException(ErrorCode.E404, LogLevel.INFO, "MessageOutbox not found messageOutboxId = " + id)
        );
    }

}
