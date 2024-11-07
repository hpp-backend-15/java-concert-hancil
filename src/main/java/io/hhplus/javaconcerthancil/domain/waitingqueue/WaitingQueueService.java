package io.hhplus.javaconcerthancil.domain.waitingqueue;

import io.hhplus.javaconcerthancil.infrastructure.persistence.waitingqueue.QueueTokenRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class WaitingQueueService {

    private final WaitingQueueRepository queueRepository;
    private final QueueTokenRedisRepository queueTokenRedisRepository;

    public String issueTokenWithRedis(Long userId) {
        return queueTokenRedisRepository.saveWaitingQueueToken(userId);
    }

    public Long getWaitingNumberWithRedis(String token) {
        Long rank = queueTokenRedisRepository.getWaitingNumber(token);
        if(rank == 0){
            queueTokenRedisRepository.isInActivationQueue(token);
        }
        return rank;
    }

    public void ensureTokenIsActiveWithRedis(String token) {
        queueTokenRedisRepository.isInActivationQueue(token);

    }

    public void periodicallyEnterWaitingQueue() {

        // 임시
        final Long maxActiveSize = 50L;

        long currentActiveSize = queueTokenRedisRepository.getActiveKeys().size();
        long availableSlots = maxActiveSize - currentActiveSize;

        // 50명이 이미 활성화되어 있는 경우, 추가로 이동할 필요 없음
        if (availableSlots <= 0) {
            log.info("현재 활성화 큐에 50명이 모두 활성화되어 있습니다.");
            return;
        }
        // 전체 사이즈를 조회후
        Set<String> range = queueTokenRedisRepository.getWaitingMembers(0, -1);

        // 대기열 큐에서 가져올 수 있는 최대 토큰 수 계산
        Set<String> waitingTokens = queueTokenRedisRepository.getWaitingMembers(0, Math.min(availableSlots - 1, range.size() - 1));
        if (!waitingTokens.isEmpty()) {
            // 대기열에서 토큰 제거 및 활성화 큐에 추가
            queueTokenRedisRepository.deleteWaitingTokens(waitingTokens);
            waitingTokens.forEach(token -> queueTokenRedisRepository.activateTokenFromWaitingQueue(token));
            log.info("대기열에서 {}명의 사용자를 활성화했습니다. 현재 활성화 큐 크기: {}", waitingTokens.size(), currentActiveSize + waitingTokens.size());
        }
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

}
