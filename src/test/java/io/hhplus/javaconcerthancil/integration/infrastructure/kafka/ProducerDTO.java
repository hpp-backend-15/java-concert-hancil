package io.hhplus.javaconcerthancil.integration.infrastructure.kafka;

public class ProducerDTO {

    private String topic;
    private String key;
    private String message;

    public ProducerDTO(String topic, String key, String message) {
        this.topic = topic;
        this.key = key;
        this.message = message;
    }

    public String getTopic() {
        return topic;
    }

    public String getKey() {
        return key;
    }

    public String getMessage() {
        return message;
    }
}
