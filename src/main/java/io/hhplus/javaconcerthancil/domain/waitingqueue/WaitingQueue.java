package io.hhplus.javaconcerthancil.domain.waitingqueue;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WaitingQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @Enumerated(EnumType.STRING)
    private QueueStatus status;

    private LocalDateTime enteredAt;
    private LocalDateTime expiredAt;

    public WaitingQueue(final String token){
        this.token = token;
        this.status = QueueStatus.WAITING;
        this.enteredAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }


}
