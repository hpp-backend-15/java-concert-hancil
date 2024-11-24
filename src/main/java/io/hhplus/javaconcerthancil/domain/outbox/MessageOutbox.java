package io.hhplus.javaconcerthancil.domain.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
@Builder
public class MessageOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String topic;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private String messageKey;

    @Enumerated(EnumType.STRING)
    private MessageStatus messageStatus = MessageStatus.PENDING;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @Column(columnDefinition = "LONGTEXT", nullable = true)
    private String errorMessage;

    public static MessageOutbox createMessage(String topic, EventType eventType, String messageKey) {
        return MessageOutbox.builder()
                .topic(topic)
                .eventType(eventType)
                .messageKey(messageKey)
                .messageStatus(MessageStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void sendSuccess() {
        LocalDateTime now = LocalDateTime.now();
        this.messageStatus = MessageStatus.SEND_SUCCESS;
        this.updatedAt = now;
    }

    public void sendFail(String errorMessage) {
        LocalDateTime now = LocalDateTime.now();
        this.messageStatus = MessageStatus.SEND_FAIL;
        this.updatedAt = now;
        this.errorMessage = errorMessage;
    }


}
