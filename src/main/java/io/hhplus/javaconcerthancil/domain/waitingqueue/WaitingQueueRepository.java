package io.hhplus.javaconcerthancil.domain.waitingqueue;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WaitingQueueRepository extends JpaRepository<WaitingQueue, Long> {

    Optional<WaitingQueue> findFirstByUserIdOrderByIdDesc(Long userId);

    List<WaitingQueue> findAllByStatusIn(List<QueueStatus> statuses);

    WaitingQueue findByToken(String token);


    long countByStatusAndIdLessThan(QueueStatus queueStatus, Long queueId);
}
