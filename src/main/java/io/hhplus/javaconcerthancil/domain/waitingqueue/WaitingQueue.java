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

    private Long userId;

    private String token;

    @Enumerated(EnumType.STRING)
    private QueueStatus status;

    private LocalDateTime enteredAt;
    private LocalDateTime expiredAt;

    public WaitingQueue(final Long userId, final String token){
        this.userId = userId;
        this.token = token;
        this.status = QueueStatus.STANDBY;
        this.enteredAt = LocalDateTime.now();
//        this.expiredAt = LocalDateTime.now().plusMinutes(30);
    }

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }
    public LocalDateTime getExpiredAt() {
        return expiredAt;
    }

    public QueueStatus getStatus() {
        return status;
    }

    public void setStatus(QueueStatus status) {
        this.status = status;
    }

    public void setExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }
}
