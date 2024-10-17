package io.hhplus.javaconcerthancil.domain.waitingqueue;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TokenProviderImpl implements WaitingQueueTokenProvider{
    @Override
    public String createQueueToken() {
        return UUID.randomUUID().toString();
    }
}
