package io.hhplus.javaconcerthancil.infrastructure.persistence.waitingqueue;

import io.hhplus.javaconcerthancil.domain.waitingqueue.QueueStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class ActiveQueueRedisRepository {

    @Qualifier("waitingQueueRedisTemplate")
    private final RedisTemplate<String, String> waitingQueueRedisTemplate;

    public void addActiveToken(String token) {
        waitingQueueRedisTemplate.opsForValue().set(token, QueueStatus.PROGRESS.toString(),Duration.ofMinutes(30L));
    }
}
