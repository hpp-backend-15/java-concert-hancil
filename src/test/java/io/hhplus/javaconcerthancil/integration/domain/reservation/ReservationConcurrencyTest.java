package io.hhplus.javaconcerthancil.integration.domain.reservation;

import io.hhplus.javaconcerthancil.domain.concert.SeatRepository;
import io.hhplus.javaconcerthancil.domain.reservation.ReservationRepository;
import io.hhplus.javaconcerthancil.domain.reservation.ReservationService;
import io.hhplus.javaconcerthancil.support.DummyDataLoaderService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ReservationConcurrencyTest {

    @Autowired
    private DummyDataLoaderService dummyDataLoaderService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @BeforeEach
    void setUp() {
        dummyDataLoaderService.loadDummyData();
    }


    @Test
    @Transactional
    public void 동시성_좌석예약_비관적락_테스트() throws InterruptedException {

        // 테스트할 좌석 ID 및 사용자 ID
        List<Long> seatIds = List.of(1L, 2L, 3L);
        long[] userIds = {1,2,3,4};
        int totalUsers = userIds.length;
        Long concertId = 1L;
        Long scheduleId = 1L;

        // 동시성 테스트를 위한 스레드 풀
        ExecutorService executorService = Executors.newFixedThreadPool(totalUsers);
        CountDownLatch latch = new CountDownLatch(totalUsers);

        // 사용자 1 예약 시도
        for(int i = 0; i < totalUsers; i++) {
            int finalI = i;
            executorService.execute(() -> {
                try {
                    reservationService.reserveConcert(userIds[finalI], concertId, scheduleId, seatIds);
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드가 작업을 완료할 때까지 대기

        // 예약 완료된 좌석 상태 확인
        assertThat(reservationRepository.count()).isEqualTo(1);
    }
}
