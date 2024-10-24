package io.hhplus.javaconcerthancil.domain.waitingqueue;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WaitingQueueService {

    private final WaitingQueueTokenProvider tokenProvider;
    private final WaitingQueueRepository queueRepository;

    public String issueToken(Long userId) {
        //1. 토큰을 발급한다.
        String queueToken = tokenProvider.createQueueToken();

        //2. 토큰을 저장한다.
        WaitingQueue waitingQueue = new WaitingQueue(userId, queueToken);
        queueRepository.save(waitingQueue);
        //3. 저장결과를 반환한다
        return queueToken;
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
