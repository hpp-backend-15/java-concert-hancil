package io.hhplus.javaconcerthancil.integration.domain.user;

import io.hhplus.javaconcerthancil.application.user.UserFacade;
import io.hhplus.javaconcerthancil.domain.user.User;
import io.hhplus.javaconcerthancil.domain.user.UserRepository;
import io.hhplus.javaconcerthancil.domain.user.UserWithVersion;
import io.hhplus.javaconcerthancil.domain.user.UserWithVersionRepository;
import io.hhplus.javaconcerthancil.interfaces.api.v1.user.request.ChargeRequest;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserBalanceConcurrencyTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserWithVersionRepository userWithVersionRepository;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private UserFacade userFacade;

    private final int numberOfThreads = 3;

    @BeforeEach
    @Transactional
    void setUp() {

        for (int i = 0; i < 100; i++) {
            User user = new User("" + i);
            userRepository.save(user);
            UserWithVersion userWithVersion = new UserWithVersion("" + i);
            userWithVersionRepository.save(userWithVersion);
        }
    }

    @AfterEach
    @Transactional
    void tearDown() {
        userRepository.deleteAll();
        userWithVersionRepository.deleteAll();
    }


//    @Test
//    @Order(1)
    void 낙관락테스트() throws InterruptedException {
        List<UserWithVersion> users = userWithVersionRepository.findAll();

        ChargeRequest request = new ChargeRequest(1000); // 충전할 금액

        int numberOfThreads = users.size();
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            long finalI = users.get(i).getId() ;
            executor.submit(() -> {
                try {
                    userFacade.chargeWithOptimisticLock(finalI, request);
                } catch (OptimisticLockException e) {
                    // 낙관적 잠금 예외 처리 로직 (필요 시 로그 추가)
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드가 완료될 때까지 대기

        // 최종 잔액 확인
        List<UserWithVersion> afterUsers = userWithVersionRepository.findAll();
        for(UserWithVersion user : afterUsers) {
            assertThat(user.getBalance()).isEqualTo(1000);
        }
    }

//    @Test
//    @Order(2)
    void 비관락테스트() throws InterruptedException {
        List<User> users = userRepository.findAll();

        ChargeRequest request = new ChargeRequest(1000); // 충전할 금액

        int numberOfThreads = users.size();
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            long finalI = users.get(i).getId() ;
            executor.submit(() -> {
                try {
                    userFacade.chargeWithPessimisticLock(finalI, request);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드가 완료될 때까지 대기

        // 최종 잔액 확인
        List<User> afterUsers = userRepository.findAll();
        for(User user : afterUsers) {
            assertThat(user.getBalance()).isEqualTo(1000);
        }
    }

    @Test
    @Order(3)
    void 비관적따닥() throws InterruptedException {
        User jhc = userRepository.save(new User("JHC"));

        ChargeRequest request = new ChargeRequest(1000); // 충전할 금액
//        numberOfThreads = 5;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                try {
                    userFacade.chargeWithPessimisticLock(jhc.getId(), request);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드가 완료될 때까지 대기

        // 최종 잔액 확인
        Optional<User> byId = userRepository.findById(jhc.getId());
        assertThat(byId.isPresent()).isTrue();
        User savedUser = byId.get();
        assertThat(savedUser.getBalance()).isEqualTo(numberOfThreads * 1000);

    }

    @Test
    @Order(4)
    void 낙관적따닥() throws InterruptedException {
        UserWithVersion jhc = userWithVersionRepository.save(new UserWithVersion("JHC"));
        Long testId = jhc.getId();
        ChargeRequest request = new ChargeRequest(1000); // 충전할 금액
//        numberOfThreads = 5;
        int maxAttempts = 3;   // 최대 시도 횟수
        long delay = 100;

        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                AtomicInteger attempts = new AtomicInteger();
                boolean success = false;

                while (attempts.get() < maxAttempts && !success) {
                    try {
                        userFacade.chargeWithOptimisticLock(testId, request);
                        success = true;  // 성공 시 반복문 탈출
                    } catch (OptimisticLockingFailureException e) {
                        attempts.getAndIncrement();
                        System.out.println("충돌! 재시도 중... (" + attempts + "/" + maxAttempts + ")");

                        if (attempts.get() < maxAttempts) {
                            try {
                                Thread.sleep(delay);  // 딜레이 후 재시도
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                            }
                        }
                        e.printStackTrace();
                    }
                }
                latch.countDown();
            });
        }

        latch.await(); // 모든 스레드가 완료될 때까지 대기

        // 최종 잔액 확인
        Optional<UserWithVersion> byId = userWithVersionRepository.findById(jhc.getId());
        assertThat(byId.isPresent()).isTrue();
        UserWithVersion savedUser = byId.get();

        //결과를 보장받지 못함
        assertThat(savedUser.getBalance()).isEqualTo(numberOfThreads * 1000);

    }


    @Test
    @Order(5)
    void 분산락따닥() throws InterruptedException {

        User jhc = userRepository.save(new User("JHC"));
        ChargeRequest chargeRequest = new ChargeRequest(1000);

        // 10개의 스레드로 동시에 요청 보내기
//        int numberOfThreads = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            long userId = jhc.getId();
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
        User user1 = userRepository.findById(jhc.getId()).orElseThrow();
        System.out.println(user1.getBalance());

        assertThat(user1.getBalance()).isEqualTo(1000);
    }


}
