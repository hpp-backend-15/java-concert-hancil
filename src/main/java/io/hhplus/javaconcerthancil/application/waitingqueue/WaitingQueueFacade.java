package io.hhplus.javaconcerthancil.application.waitingqueue;

import io.hhplus.javaconcerthancil.domain.waitingqueue.WaitingQueueService;
import io.hhplus.javaconcerthancil.interfaces.api.v1.waitingqueue.response.IssueTokenResponse;
import io.hhplus.javaconcerthancil.interfaces.api.v1.waitingqueue.response.QueueStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WaitingQueueFacade {

    private final WaitingQueueService waitingQueueService;

    public IssueTokenResponse issueToken(Long userId){
        return new IssueTokenResponse(
                waitingQueueService.issueTokenWithRedis(userId)
        );
    }

    public QueueStatusResponse getTokenInfo(String token) {
        Long waitingNumber = waitingQueueService.getWaitingNumberWithRedis(token);
        return new QueueStatusResponse(waitingNumber);
    }
}
