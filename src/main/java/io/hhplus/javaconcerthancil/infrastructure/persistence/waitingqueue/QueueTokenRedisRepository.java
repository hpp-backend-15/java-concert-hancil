package io.hhplus.javaconcerthancil.infrastructure.persistence.waitingqueue;

import java.util.Set;

public interface QueueTokenRedisRepository {
    String saveWaitingQueueToken(Long userId);
    Long getWaitingNumber(String token);
    Set<String> getWaitingMembers(long start, long end);
    void deleteWaitingTokens(Set<String> tokens);
    Set<String> getActiveKeys();
    void activateTokenFromWaitingQueue(String token);
    void isInActivationQueue(String token);
    void deleteActiveToken(String token);
    void deleteAll();
}
