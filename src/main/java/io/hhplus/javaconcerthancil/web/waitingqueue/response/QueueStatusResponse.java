package io.hhplus.javaconcerthancil.web.waitingqueue.response;

import io.hhplus.javaconcerthancil.domain.waitingqueue.QueueStatus;

import java.time.LocalDateTime;

public record QueueStatusResponse(Long totalInQueue, Long currentPosition, LocalDateTime expiration, QueueStatus status) {
}
