package io.hhplus.javaconcerthancil.integration.domain.user;

import io.hhplus.javaconcerthancil.application.user.UserFacade;
import io.hhplus.javaconcerthancil.domain.user.User;
import io.hhplus.javaconcerthancil.domain.user.UserRepository;
import io.hhplus.javaconcerthancil.interfaces.api.v1.user.request.ChargeRequest;
import io.hhplus.javaconcerthancil.support.DummyDataLoaderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class UserBalanceChargeIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private DummyDataLoaderService dummyDataLoaderService;

    @BeforeEach
    void setUp() {
        dummyDataLoaderService.loadDummyData();
    }

    @Test
    void 동시_충전() throws InterruptedException {

        Long[] userIds = {1L,2L,3L,1L,1L,1L,3L,4L,2L,1L}; // 테스트 사용자 ID
        ChargeRequest request = new ChargeRequest(1000); // 충전할 금액

        // 10개의 스레드로 동시에 요청 보내기
        int numberOfThreads = userIds.length;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            int finalI = i;
            executor.submit(() -> {
                try {
                    userFacade.charge(userIds[finalI], request);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드가 완료될 때까지 대기

        // 최종 잔액 확인
        User user1 = userRepository.findById(1L).orElseThrow();
        User user2 = userRepository.findById(2L).orElseThrow();
        User user3 = userRepository.findById(3L).orElseThrow();
        User user4 = userRepository.findById(4L).orElseThrow();
        assertThat(user1.getBalance()).isEqualTo(5*1000);
        assertThat(user2.getBalance()).isEqualTo(2*1000);
        assertThat(user3.getBalance()).isEqualTo(2*1000);
        assertThat(user4.getBalance()).isEqualTo(1*1000);


    }

}
