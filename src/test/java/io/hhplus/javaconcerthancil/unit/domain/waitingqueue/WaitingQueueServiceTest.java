package io.hhplus.javaconcerthancil.unit.domain.waitingqueue;

import io.hhplus.javaconcerthancil.domain.waitingqueue.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WaitingQueueServiceTest {

    @Autowired
    private WaitingQueueTokenProvider tokenProvider;

    @Autowired
    private WaitingQueueRepository queueRepository;

    @Autowired
    private WaitingQueueService waitingQueueService;

    @BeforeEach
    void setUp() {
        waitingQueueService = new WaitingQueueService(tokenProvider, queueRepository);
        queueRepository.save(new WaitingQueue(1L, "token1"));
    }

    @Test
    @DisplayName("토큰을 발급받는다.")
    @Transactional
    void issueToken1() {

        // Given
        Long userId = 99L;

        // When
        String issueToken = waitingQueueService.issueToken(userId);

        // Then
        assertNotNull(issueToken);
    }

    @Test
    @DisplayName("queue에 해당 유저의 토큰이 존재할 경우 기존 대기열 토큰을 반환한다.")
    @Transactional
    void issueToken2() {
        //given
        Long userId = 1L;

        //when
        Optional<WaitingQueue> tokenByUserId = waitingQueueService.getTokenByUserId(userId);

        //then
        assertTrue(tokenByUserId.isPresent());

        assertThat(tokenByUserId.get().getToken()).isEqualTo("token1");
    }


    @Test
    @DisplayName("토큰으로_대기열엔티티_조회")
    @Transactional
    void findByTokenTest() {
        //given
        String givenToken = "token1";

        //when
        WaitingQueue byToken = waitingQueueService.findByToken(givenToken);

        //then
        assertNotNull(byToken);
        assertThat(byToken.getToken()).isEqualTo("token1");
        assertThat(byToken.getStatus()).isEqualTo(QueueStatus.STANDBY);
    }

    @Test
    @DisplayName("대기번호조회")
    @Transactional
    void getWaitingNumberTest() {
        //given
        Long userId = 1L;

        //when
        long waitingNumber = waitingQueueService.getWaitingNumber(userId);

        //then
        assertNotNull(waitingNumber);
        assertThat(waitingNumber).isEqualTo(0);

    }

    @Test
    @DisplayName("대기열 활성화 업데이트")
    @Transactional
    void updateTokenActivateTest() {

        // Given
        Long userId = 1L;
        Optional<WaitingQueue> optionalWaitingQueue = waitingQueueService.getTokenByUserId(userId);
        assertTrue(optionalWaitingQueue.isPresent());

        WaitingQueue queueItem = optionalWaitingQueue.get();

        // When
        WaitingQueue waitingQueue = waitingQueueService.updateTokenActivate(queueItem);

        //then
        assertThat(waitingQueue.getToken()).isEqualTo("token1");
        assertThat(waitingQueue.getStatus()).isEqualTo(QueueStatus.PROGRESS);

    }



}
