package io.hhplus.javaconcerthancil.unit.domain.waitingqueue;

import io.hhplus.javaconcerthancil.domain.waitingqueue.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

public class WaitingQueueServiceUnitTest {


    @Mock
    private WaitingQueueTokenProvider tokenProvider;  // 모킹된 토큰 제공자

    @Mock
    private WaitingQueueRepository queueRepository;   // 모킹된 레포지토리

    @InjectMocks
    private WaitingQueueService waitingQueueService;  // 테스트할 서비스

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Mockito 초기화
    }

    @Test
    @DisplayName("토큰을 발급받는다.")
    void issueToken1() {
        // Given
        Long userId = 99L;
        String generatedToken = "newToken";

        // 토큰 생성 로직을 모킹
        given(tokenProvider.createQueueToken()).willReturn(generatedToken);

        // When
        String issueToken = waitingQueueService.issueToken(userId);

        // Then
        assertNotNull(issueToken);
        assertThat(issueToken).isEqualTo(generatedToken);  // 모킹된 토큰이 반환되는지 검증
    }

    @Test
    @DisplayName("queue에 해당 유저의 토큰이 존재할 경우 기존 대기열 토큰을 반환한다.")
    void issueToken2() {
        // Given
        Long userId = 1L;
        WaitingQueue existingQueue = new WaitingQueue(userId, "token1");

        // 모킹: 해당 유저의 대기열에 이미 토큰이 있을 때
        given(queueRepository.findFirstByUserIdAndStatusInOrderByIdDesc(userId, Arrays.asList(QueueStatus.STANDBY, QueueStatus.PROGRESS)))
                .willReturn(Optional.of(existingQueue));

        // When
        Optional<WaitingQueue> tokenByUserId = waitingQueueService.getTokenByUserId(userId);

        // Then
        assertTrue(tokenByUserId.isPresent());
        assertThat(tokenByUserId.get().getToken()).isEqualTo("token1");
    }

    @Test
    @DisplayName("토큰으로 대기열 엔티티를 조회")
    void findByTokenTest() {
        // Given
        String givenToken = "token1";
        WaitingQueue queueItem = new WaitingQueue(1L, givenToken);

        // 모킹: 토큰으로 대기열 찾기
        given(queueRepository.findByToken(givenToken)).willReturn(queueItem);

        // When
        WaitingQueue byToken = waitingQueueService.findByToken(givenToken);

        // Then
        assertNotNull(byToken);
        assertThat(byToken.getToken()).isEqualTo("token1");
        assertThat(byToken.getStatus()).isEqualTo(QueueStatus.STANDBY);  // 상태 검증
    }

    @Test
    @DisplayName("대기번호 조회")
    void getWaitingNumberTest() {
        // Given
        Long userId = 1L;
        Long queueId = 1L;
        String givenToken = "token1";
        WaitingQueue queueItem = new WaitingQueue(queueId, userId, givenToken);

        // 모킹: 유저 ID로 대기열 찾기
        given(queueRepository.countByStatusAndIdLessThan(QueueStatus.STANDBY, queueId)).willReturn(1L);

        // When
        long waitingNumber = waitingQueueService.getWaitingNumber(queueId);

        // Then
        assertThat(waitingNumber).isEqualTo(1);  // 대기번호가 0인지 검증
    }

    @Test
    @DisplayName("대기열 활성화 업데이트")
    void updateTokenActivateTest() {
        // Given
        Long userId = 1L;
        WaitingQueue queueItem = new WaitingQueue(userId, "token1");
        queueItem.setStatus(QueueStatus.PROGRESS);
        queueItem.setExpiredAt(LocalDateTime.now().plusMinutes(30));

        // 모킹: 해당 유저의 대기열에 토큰이 있을 때
        given(queueRepository.save(queueItem)).willReturn(queueItem);

        // When
        WaitingQueue updatedQueue = waitingQueueService.updateTokenActivate(queueItem);

        // Then
        assertThat(updatedQueue.getToken()).isEqualTo("token1");
        assertThat(updatedQueue.getStatus()).isEqualTo(QueueStatus.PROGRESS);  // 상태가 진행 중(PROGRESS)인지 검증
    }
}
