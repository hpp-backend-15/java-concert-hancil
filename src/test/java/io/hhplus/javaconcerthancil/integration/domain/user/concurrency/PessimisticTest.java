package io.hhplus.javaconcerthancil.integration.domain.user.concurrency;

import io.hhplus.javaconcerthancil.application.user.UserFacade;
import io.hhplus.javaconcerthancil.domain.user.User;
import io.hhplus.javaconcerthancil.domain.user.UserRepository;
import io.hhplus.javaconcerthancil.interfaces.api.v1.user.request.ChargeRequest;
import jakarta.transaction.Transactional;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PessimisticTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFacade userFacade;

    @BeforeAll
    @Transactional
    void setUp() {
        String[] userNames = {"JHC", "MMA", "CLIMBING", "MAN"};
        for(String name : userNames) {
            User user = new User(name);
            userRepository.save(user);
        }
    }


    @Test
    void 비관적_충전() throws InterruptedException {

        Long[] userIds = {1L,2L,3L,1L,1L,1L,3L,4L,2L,1L}; // 테스트 사용자 ID
        ChargeRequest chargeRequest = new ChargeRequest(1000);

        // 10개의 스레드로 동시에 요청 보내기
        int numberOfThreads = userIds.length;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            int finalI = i;
            long userId = userIds[finalI];
            executor.submit(() -> {
                try {
                    userFacade.chargeWithPessimisticLock(userId, chargeRequest);
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

        assertAll(
                () -> AssertionsForClassTypes.assertThat(user1.getBalance()).isEqualTo(5*1000),
                () -> AssertionsForClassTypes.assertThat(user2.getBalance()).isEqualTo(2*1000),
                () -> AssertionsForClassTypes.assertThat(user3.getBalance()).isEqualTo(2*1000),
                () -> AssertionsForClassTypes. assertThat(user4.getBalance()).isEqualTo(1*1000)
                );

    }





}
