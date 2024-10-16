package io.hhplus.javaconcerthancil.application.waitingqueue;

import io.hhplus.javaconcerthancil.domain.waitingqueue.WaitingQueue;
import io.hhplus.javaconcerthancil.domain.waitingqueue.WaitingQueueService;
import io.hhplus.javaconcerthancil.web.waitingqueue.response.IssueTokenResponse;
import io.hhplus.javaconcerthancil.web.waitingqueue.response.QueueStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class WaitingQueueFacade {

    private final WaitingQueueService waitingQueueService;
    private static final int MAX = 50;

    public IssueTokenResponse issueToken(Long userId){

        //1. queue에 해당 유저의 토큰이 존재할 경우
        // 기존 대기열 토큰을 반환한다.
        Optional<WaitingQueue> tokenByUserId = waitingQueueService.getTokenByUserId(userId);
        if(tokenByUserId.isPresent()){
            return new IssueTokenResponse(tokenByUserId.get().getToken());
        }

        //2. 대기열 토큰이 존재하지 않을 경우
        // 신규 대기열 토큰을 반환한다.
        return new IssueTokenResponse(
                waitingQueueService.issueToken(userId)
        );
    }

    public QueueStatusResponse getTokenInfo(String token) {

        // 토큰으로 대기열 엔티티 조회
        WaitingQueue queueItem = waitingQueueService.findByToken(token);

        if (queueItem == null) {
            throw new IllegalArgumentException("Invalid token: " + token);
        }

        long waitingNumber = waitingQueueService.getWaitingNumber(queueItem.getId());

        // 대기열에서 기다리지 않고 통과할 수 있는 경우
        if(waitingNumber < MAX){

            // 대기열 토큰 만료 시간을 현재시간 + 30 분으로 업데이트
            // 대기열 토큰의 진행 상태를 PROGRESS로 업데이트
            WaitingQueue waitingQueue = waitingQueueService.updateTokenActivate(queueItem);

            // currentPosition = 0 으로 반환
            return new QueueStatusResponse(0L);
        }

        //제한 인원이 대기열에 꽉차 대기열을 기다려야 하는 경우
        // currentPosition 을 반환
        return new QueueStatusResponse(waitingNumber+1);

    }
}
