package io.hhplus.javaconcerthancil.infrastructure.kafka.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Getter
@NoArgsConstructor
@ToString
public class KafkaMessage<T> {

    private Long publishTimestamp;
    private Long createTimestamp;
    private String eventKey;
    private T payload;

    public KafkaMessage(Long publishTimestamp, Long createTimestamp, T payload) {
        this.publishTimestamp = publishTimestamp;
        this.createTimestamp = createTimestamp;
        this.eventKey = UUID.randomUUID().toString();
        this.payload = payload;
    }
}
