package io.hhplus.javaconcerthancil.integration.domain.user;

import io.hhplus.javaconcerthancil.domain.user.*;
import io.hhplus.javaconcerthancil.infrastructure.persistence.user.UserJpaRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserRepositoryTest {

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private BalanceHistoryRepository balanceHistoryRepository;

    private long testUserId;

    @BeforeEach
    @Transactional
    void setUp() {
        balanceHistoryRepository.deleteAll();
        userRepository.deleteAll();
        User user = userRepository.save(new User("JHC", 10_000));
        testUserId = user.getId();
    }


    @Test
    @DisplayName("충전했으면, 충전이력도 저장돼?")
    void balanceChargeTest() {
        //given
        Long userId = testUserId;
        int amount = 10000;

        Optional<User> optionalUser = userRepository.findById(userId);
        assertTrue(optionalUser.isPresent());


        //when
        User user = optionalUser.get();
        user.addAmount(amount);
        userRepository.save(user);

        //then
        List<BalanceHistory> balanceHistories = balanceHistoryRepository.findAll();
        for (BalanceHistory bh : balanceHistories) {
            assertThat(bh.getUser().getId()).isEqualTo(userId);
            assertThat(bh.getAmount()).isEqualTo(amount);
            assertThat(bh.getType()).isEqualTo(TransactionType.CHARGE);
        }
    }

    @Test
    @DisplayName("금액을 사용했으면, 사용이력도 저장돼?")
    void balanceUseTest() {
        //given
        Long userId = testUserId;
        int amount = 10000;

        Optional<User> optionalUser = userRepository.findById(userId);
        assertTrue(optionalUser.isPresent());


        //when
        User user = optionalUser.get();
        user.subtractAmount(amount);
        userRepository.save(user);

        //then
        List<BalanceHistory> balanceHistories = balanceHistoryRepository.findAll();
        for (BalanceHistory bh : balanceHistories) {
            assertThat(bh.getUser().getId()).isEqualTo(userId);
            assertThat(bh.getAmount()).isEqualTo(amount);
            assertThat(bh.getType()).isEqualTo(TransactionType.USE);
        }
    }
}
