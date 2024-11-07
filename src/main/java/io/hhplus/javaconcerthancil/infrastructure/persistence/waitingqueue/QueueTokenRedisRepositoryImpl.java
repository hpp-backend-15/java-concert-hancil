package io.hhplus.javaconcerthancil.infrastructure.persistence.waitingqueue;

import io.hhplus.javaconcerthancil.domain.waitingqueue.QueueStatus;
import io.hhplus.javaconcerthancil.interfaces.api.common.ApiException;
import io.hhplus.javaconcerthancil.interfaces.api.common.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.logging.LogLevel;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Set;

@Repository
@RequiredArgsConstructor
@Slf4j
public class QueueTokenRedisRepositoryImpl implements QueueTokenRedisRepository {

    private final static String WAITING_TOKENS_KEY = "WAITING";
    private final static String ACTIVE_TOKENS_KEY = "ACTIVE_";

    @Qualifier("waitingQueueRedisTemplate")
    private final RedisTemplate<String, String> waitingQueueRedisTemplate;

    @Override
    public String saveWaitingQueueToken(Long userId) {
        waitingQueueRedisTemplate.opsForZSet().add(WAITING_TOKENS_KEY, "user:" + userId, System.currentTimeMillis());
        return WAITING_TOKENS_KEY + "_" + "user:" + userId;
    }

    @Override
    public Long getWaitingNumber(String token) {
        String targetToken = validateToken(token);
        Long rank = waitingQueueRedisTemplate.opsForZSet().rank(WAITING_TOKENS_KEY, targetToken);
        if(rank == null || rank == 0) {
            return 0L;
        }
        // 0부터 시작하므로 1을 더해줌
        return rank + 1;
    }

    @Override
    public Set<String> getWaitingMembers(long start, long end) {
        return waitingQueueRedisTemplate.opsForZSet().range(WAITING_TOKENS_KEY, start, end);
    }

    @Override
    public void deleteWaitingTokens(Set<String> tokens) {
        waitingQueueRedisTemplate.opsForZSet().remove(WAITING_TOKENS_KEY, tokens.toArray());
    }

    @Override
    public Set<String> getActiveKeys(){
        return waitingQueueRedisTemplate.keys(ACTIVE_TOKENS_KEY + "*");
    }

    @Override
    public void activateTokenFromWaitingQueue(String token) {
        waitingQueueRedisTemplate.opsForValue().set(ACTIVE_TOKENS_KEY+token, QueueStatus.PROGRESS.toString(), Duration.ofMinutes(10L));
    }

    @Override
    public void isInActivationQueue(String token) {
        String targetToken = validateToken(token);
        String key = ACTIVE_TOKENS_KEY + targetToken;

        Boolean isInActivationQueue = waitingQueueRedisTemplate.hasKey(key);
        if(Boolean.FALSE.equals(isInActivationQueue)){
            throw new ApiException(ErrorCode.E001, LogLevel.INFO, "token: " + token);
        }
    }

    @Override
    public void deleteActiveToken(String token) {
        String targetToken = validateToken(token);
        waitingQueueRedisTemplate.delete(targetToken);
    }

    @Override
    public void deleteAll() {
        Set<String> keys = waitingQueueRedisTemplate.keys("*");
        if (keys != null) {
            for (String key : keys) {
                waitingQueueRedisTemplate.delete(key);
            }
        }
    }

    private static String validateToken(String token) {

        if (token == null || !token.contains("_")) {
            throw new ApiException(ErrorCode.E006, LogLevel.ERROR, "유효하지 않은 요청입니다.");
        }

        String[] splitToken = token.split("_");
        if (splitToken.length < 2 && !WAITING_TOKENS_KEY.equals(splitToken[0])) {
            throw new ApiException(ErrorCode.E006, LogLevel.ERROR, "유효하지 않은 요청입니다.");
        }
        return splitToken[1];
    }



}

