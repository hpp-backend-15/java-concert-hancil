package io.hhplus.javaconcerthancil.unit.infrastructure;

import io.hhplus.javaconcerthancil.support.dto.ProducerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KafkaSerializeTest {
    byte[] serialized;

    @BeforeEach
    void setUp() {
        ProducerDTO producerDTO = new ProducerDTO("TEST","KEY","MESSAGE");
        JsonSerializer jsonSerializer = new JsonSerializer();
        this.serialized = jsonSerializer.serialize("TEST", producerDTO);
    }

    @Test
    void name() {
        JsonDeserializer<ProducerDTO> deserializer = new JsonDeserializer<>(ProducerDTO.class, false);
        ProducerDTO producerDTO = deserializer.deserialize("TEST", serialized);
        assertThat(producerDTO.getMessage()).isEqualTo("MESSAGE");
    }


}
