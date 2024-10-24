package io.hhplus.javaconcerthancil.domain.waitingqueue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WaitingQueueRepository extends JpaRepository<WaitingQueue, Long> {

    Optional<WaitingQueue> findFirstByUserIdAndStatusInOrderByIdDesc(Long userId, List<QueueStatus> queueStatuses);
    WaitingQueue findByToken(String token);
    long countByStatusAndIdLessThan(QueueStatus queueStatus, Long queueId);

    @Modifying
    @Query("UPDATE WaitingQueue wq SET wq.status = 'EXPIRED' WHERE wq.expiredAt < :now AND wq.status = 'PROGRESS'")
    int updateExpiredQueues(LocalDateTime now);
}
