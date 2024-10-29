package io.hhplus.javaconcerthancil.integration.domain.user.concurrency;

import io.hhplus.javaconcerthancil.application.user.UserFacade;
import io.hhplus.javaconcerthancil.domain.user.User;
import io.hhplus.javaconcerthancil.domain.user.UserRepository;
import io.hhplus.javaconcerthancil.domain.user.UserWithVersion;
import io.hhplus.javaconcerthancil.domain.user.UserWithVersionRepository;
import io.hhplus.javaconcerthancil.interfaces.api.v1.user.request.ChargeRequest;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OptimisticTest {

    @Autowired
    private UserWithVersionRepository userWithVersionRepository;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private UserFacade userFacade;

    @BeforeAll
    @Transactional
    void setUp() {
        String[] userNames = {"JHC", "MMA", "CLIMBING", "MAN"};
        for(String name : userNames) {
            UserWithVersion userWithVersion = new UserWithVersion(name);
            userWithVersionRepository.save(userWithVersion);
        }
    }

    @Test
    void 낙관적_충전() throws InterruptedException {

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
                    userFacade.chargeWithOptimisticLock(userId, chargeRequest);
                } catch (OptimisticLockingFailureException e) {
                    System.out.println("충돌!");
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드가 완료될 때까지 대기

        // 최종 잔액 확인
        UserWithVersion user1 = userWithVersionRepository.findById(1L).orElseThrow();
        UserWithVersion user2 = userWithVersionRepository.findById(2L).orElseThrow();
        UserWithVersion user3 = userWithVersionRepository.findById(3L).orElseThrow();
        UserWithVersion user4 = userWithVersionRepository.findById(4L).orElseThrow();

        System.out.println(user1.getBalance());
        System.out.println(user2.getBalance());
        System.out.println(user3.getBalance());
        System.out.println(user4.getBalance());


        assertAll(
                () -> AssertionsForClassTypes.assertThat(user1.getBalance()).isEqualTo(5*1000),
                () -> AssertionsForClassTypes.assertThat(user2.getBalance()).isEqualTo(2*1000),
                () -> AssertionsForClassTypes.assertThat(user3.getBalance()).isEqualTo(2*1000),
                () -> AssertionsForClassTypes. assertThat(user4.getBalance()).isEqualTo(1*1000)
        );

    }

    @Test
    void 낙관적_충전2() throws InterruptedException {
        Long[] userIds = {1L, 2L, 3L, 1L, 1L, 1L, 3L, 4L, 2L, 1L}; // 테스트 사용자 ID
        ChargeRequest chargeRequest = new ChargeRequest(1000);
        int maxRetries = 3; // 최대 재시도 횟수

        // 10개의 스레드로 동시에 요청 보내기
        int numberOfThreads = userIds.length;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            int finalI = i;
            long userId = userIds[finalI];
            executor.submit(() -> {
                int attempt = 0;
                boolean success = false;

                while (attempt < maxRetries && !success) {
                    try {
                        userFacade.chargeWithOptimisticLock(userId, chargeRequest);
                        success = true; // 성공적으로 처리된 경우
                    } catch (OptimisticLockingFailureException e) {
                        attempt++;
                        System.out.println("충돌 발생, 재시도: " + attempt);
                        // 잠시 대기 (옵션, 필요에 따라 조절)
                        try {
                            Thread.sleep(100); // 100ms 후 재시도
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt(); // 인터럽트 상태 복원
                        }
                    } catch (Exception e) {
                        System.out.println("기타 예외 발생: " + e.getMessage());
                        break; // 다른 예외 발생 시 루프 종료
                    } finally {
                        latch.countDown();
                    }
                }
            });
        }

        latch.await(); // 모든 스레드가 완료될 때까지 대기

        // 최종 잔액 확인
        UserWithVersion user1 = userWithVersionRepository.findById(1L).orElseThrow();
        UserWithVersion user2 = userWithVersionRepository.findById(2L).orElseThrow();
        UserWithVersion user3 = userWithVersionRepository.findById(3L).orElseThrow();
        UserWithVersion user4 = userWithVersionRepository.findById(4L).orElseThrow();

        System.out.println(user1.getBalance());
        System.out.println(user2.getBalance());
        System.out.println(user3.getBalance());
        System.out.println(user4.getBalance());

        assertAll(
                () -> AssertionsForClassTypes.assertThat(user1.getBalance()).isEqualTo(5 * 1000),
                () -> AssertionsForClassTypes.assertThat(user2.getBalance()).isEqualTo(2 * 1000),
                () -> AssertionsForClassTypes.assertThat(user3.getBalance()).isEqualTo(2 * 1000),
                () -> AssertionsForClassTypes.assertThat(user4.getBalance()).isEqualTo(1 * 1000)
        );
    }


    @Test
    void 분산락_충전3() throws InterruptedException {

        Long[] userIds = {1L, 2L, 3L, 1L, 1L, 1L, 3L, 4L, 2L, 1L}; // 테스트 사용자 ID
        ChargeRequest chargeRequest = new ChargeRequest(1000);

        // 10개의 스레드로 동시에 요청 보내기
        int numberOfThreads = userIds.length;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            int finalI = i;
            long userId = userIds[finalI];
            executor.submit(() -> {
                RLock lock = redissonClient.getLock("lock:user:" + userId); // 유저 ID에 대한 락 생성
                try {
                    if (lock.tryLock()) { // 락 획득
                        userFacade.charge(userId, chargeRequest);
                    } else {
                        System.out.println("충돌! 이미 처리 중인 요청입니다."); // 락을 획득하지 못한 경우
                    }
                } catch (Exception e) {
                    System.out.println("충돌 발생: " + e.getMessage());
                } finally {
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock(); // 락 해제
                    }
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드가 완료될 때까지 대기

        // 최종 잔액 확인
        UserWithVersion user1 = userWithVersionRepository.findById(1L).orElseThrow();
        UserWithVersion user2 = userWithVersionRepository.findById(2L).orElseThrow();
        UserWithVersion user3 = userWithVersionRepository.findById(3L).orElseThrow();
        UserWithVersion user4 = userWithVersionRepository.findById(4L).orElseThrow();

        System.out.println(user1.getBalance());
        System.out.println(user2.getBalance());
        System.out.println(user3.getBalance());
        System.out.println(user4.getBalance());

        assertAll(
                () -> AssertionsForClassTypes.assertThat(user1.getBalance()).isEqualTo(5 * 1000),
                () -> AssertionsForClassTypes.assertThat(user2.getBalance()).isEqualTo(2 * 1000),
                () -> AssertionsForClassTypes.assertThat(user3.getBalance()).isEqualTo(2 * 1000),
                () -> AssertionsForClassTypes.assertThat(user4.getBalance()).isEqualTo(1 * 1000)
        );
    }
}
