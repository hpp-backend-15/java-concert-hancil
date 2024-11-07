package io.hhplus.javaconcerthancil.infrastructure.persistence.waitingqueue;

import io.hhplus.javaconcerthancil.interfaces.api.common.ApiException;
import io.hhplus.javaconcerthancil.interfaces.api.common.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.logging.LogLevel;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class WaitingQueueRedisRepository {

    private final static String WAITING_TOKENS_KEY = "WAITING";

    @Qualifier("waitingQueueRedisTemplate")
    private final RedisTemplate<String, String> waitingQueueRedisTemplate;

    public String add(Long userId) {
        waitingQueueRedisTemplate.opsForZSet().add(WAITING_TOKENS_KEY, "user:" + userId, System.currentTimeMillis());
        return WAITING_TOKENS_KEY + "_" + "user:" + userId;
    }

    // 0부터 시작하므로 1을 더해줌
    public Long rank(String token){
        String targetToken = validateWaitingToken(token);
        Long rank = waitingQueueRedisTemplate.opsForZSet().rank(WAITING_TOKENS_KEY, targetToken);
        if(rank == null) {
            return 0L;
        }
        return rank + 1;
    }

    public Set<String> range(long start, long end) {
        return waitingQueueRedisTemplate.opsForZSet().range(WAITING_TOKENS_KEY, start, end);
    }

    public Long delete(Set<String> tokens) {
        return waitingQueueRedisTemplate.opsForZSet().remove(WAITING_TOKENS_KEY, tokens.toArray());
    }

    public Set<String> getKeys(){
        return waitingQueueRedisTemplate.keys("*");
    }

    private static String validateWaitingToken(String token) {
        String targetToken = "";
        if(token != null && token.contains("_")){
            String[] splitToken = token.split("_");
            if(!splitToken[0].equals(WAITING_TOKENS_KEY)){
                throw new ApiException(ErrorCode.E006, LogLevel.ERROR, "유효하지 않은 요청입니다.");
            }
            targetToken = splitToken[1];
        }
        return targetToken;
    }

}

