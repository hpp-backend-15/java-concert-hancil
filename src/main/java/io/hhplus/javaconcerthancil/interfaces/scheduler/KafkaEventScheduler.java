package io.hhplus.javaconcerthancil.interfaces.scheduler;

import io.hhplus.javaconcerthancil.domain.outbox.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class KafkaEventScheduler {

    private final MessageOutboxReader messageOutboxReader;
    private final MessageOutboxWriter messageOutboxWriter;

    @Scheduled(fixedDelay = 10 * 1000)
    public void publishRetryPayment() {
        processRetryMessages(
            "ConcertPayment",
            EventType.SEND_PAYMENT_RESULT
            );
    }

    @Transactional
    protected void processRetryMessages(String topic, EventType eventType) {
        List<MessageOutbox> messageOutboxes = messageOutboxReader.findAllBy(topic, eventType);
        LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(10);

        for (MessageOutbox messageOutbox : messageOutboxes) {
            if (shouldRetryMessage(messageOutbox, tenMinutesAgo)) {
                messageOutbox.sendSuccess();
                messageOutboxWriter.save(messageOutbox);
            }
        }
    }

    private boolean shouldRetryMessage(MessageOutbox messageOutbox, LocalDateTime tenMinutesAgo) {
        return messageOutbox.getMessageStatus() != MessageStatus.SEND_SUCCESS &&
            messageOutbox.getCreatedAt().isBefore(tenMinutesAgo);
    }
}