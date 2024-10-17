package io.hhplus.javaconcerthancil.unit.domain.waitingqueue;

import io.hhplus.javaconcerthancil.domain.waitingqueue.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class WaitingQueueServiceTest {

    @InjectMocks
    private WaitingQueueService waitingQueueService;

    @Mock
    private WaitingQueueTokenProvider tokenProvider;

    @Mock
    private WaitingQueueRepository queueRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 토큰을_발급받는다() {

        // Given
        Long userId = 1L;
        String expectedToken = UUID.randomUUID().toString();
        given(tokenProvider.createQueueToken()).willReturn(expectedToken);

        // When
        String actualToken = waitingQueueService.issueToken(userId);

        // Then
        assertEquals(expectedToken, actualToken);
        verify(queueRepository, times(1)).save(any(WaitingQueue.class));
    }

    @Test
    void 대기열에서_대기중인_모든_항목_조회_테스트() {

        // Given
        WaitingQueue queueItem1 = new WaitingQueue(1L, "token1");
        WaitingQueue queueItem2 = new WaitingQueue(2L, "token2");
        given(queueRepository.findAllByStatusIn(any())).willReturn(List.of(queueItem1, queueItem2));

        // When
        List<WaitingQueue> result = waitingQueueService.getWaitingTotalInQueue();

        // Then
        assertEquals(2, result.size());
        verify(queueRepository, times(1)).findAllByStatusIn(any());
    }


}
