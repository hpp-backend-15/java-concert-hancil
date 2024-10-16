package io.hhplus.javaconcerthancil.domain.waitingqueue;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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

    public List<WaitingQueue> getWaitingTotalInQueue() {

        return queueRepository.findAllByStatusIn(
                List.of(QueueStatus.STANDBY, QueueStatus.PROGRESS)
                );
    }

    public Optional<WaitingQueue> getTokenByUserId(Long userId) {
        return queueRepository.findFirstByUserIdOrderByIdDesc(userId);
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
}
