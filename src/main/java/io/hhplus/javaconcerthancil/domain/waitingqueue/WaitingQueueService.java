package io.hhplus.javaconcerthancil.domain.waitingqueue;

import io.hhplus.javaconcerthancil.infrastructure.persistence.waitingqueue.ActiveQueueRedisRepository;
import io.hhplus.javaconcerthancil.infrastructure.persistence.waitingqueue.WaitingQueueRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class WaitingQueueService {

//    private final WaitingQueueTokenProvider tokenProvider;
    private final WaitingQueueRepository queueRepository;
    private final WaitingQueueRedisRepository waitingQueueRedisRepository;
    private final ActiveQueueRedisRepository activeQueueRedisRepository;
    public String issueToken(Long userId) {
        return waitingQueueRedisRepository.add(userId);
    }

    public Optional<WaitingQueue> getTokenByUserId(Long userId) {
        return queueRepository.findFirstByUserIdAndStatusInOrderByIdDesc(userId,
                Arrays.asList(QueueStatus.STANDBY, QueueStatus.PROGRESS));
    }

    public long getWaitingNumber(Long queueId) {
        return queueRepository.countByStatusAndIdLessThan(QueueStatus.STANDBY, queueId);
    }

    public WaitingQueue findByToken(String token) {
        return queueRepository.findByToken(token);

    }

    public WaitingQueue updateTokenActivate(WaitingQueue queueItem) {
        queueItem.setStatus(QueueStatus.PROGRESS);
        queueItem.setExpiredAt(LocalDateTime.now().plusMinutes(30));
        return queueRepository.save(queueItem);
    }

    public boolean isActiveToken(String token) {
        WaitingQueue waitingQueue = queueRepository.findByToken(token);
        return waitingQueue != null && waitingQueue.getStatus() == QueueStatus.PROGRESS;
    }

    public void updateTokenExpire(String token) {
        WaitingQueue queueItem = queueRepository.findByToken(token);
        queueItem.setStatus(QueueStatus.EXPIRED);
        queueRepository.save(queueItem);
    }

    public void periodicallyEnterWaitingQueue() {
        // 임시
        final Long maxWaitingSize = 50L;

        // 전체 사이즈를 조회후
        Set<String> range = waitingQueueRedisRepository.range(0, -1);

        // 50명만 허용
        Set<String> getWaitingTokenRange = waitingQueueRedisRepository.range(0, range.size() - maxWaitingSize -1);

        waitingQueueRedisRepository.delete(getWaitingTokenRange);
        getWaitingTokenRange.forEach(token -> {
            activeQueueRedisRepository.addActiveToken(token);
        });
    }
}
