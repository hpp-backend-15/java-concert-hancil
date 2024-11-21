package io.hhplus.javaconcerthancil.support.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProducerDTO {

    private String topic;
    private String key;
    private String message;

}
