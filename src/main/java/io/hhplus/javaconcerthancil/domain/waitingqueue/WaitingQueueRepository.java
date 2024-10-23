package io.hhplus.javaconcerthancil.domain.waitingqueue;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WaitingQueueRepository extends JpaRepository<WaitingQueue, Long> {

    Optional<WaitingQueue> findFirstByUserIdAndStatusInOrderByIdDesc(Long userId, List<QueueStatus> queueStatuses);
    WaitingQueue findByToken(String token);
    long countByStatusAndIdLessThan(QueueStatus queueStatus, Long queueId);
}
