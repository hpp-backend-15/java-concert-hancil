package io.hhplus.javaconcerthancil.unit.domain.user;

import io.hhplus.javaconcerthancil.domain.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

public class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BalanceHistoryRepository balanceHistoryRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Mockito 초기화
    }

    @Test
    void 사용자_조회() {
        //given
        int wantToCharge = 10_000;
        User user = new User("JHC");
        user.addAmount(wantToCharge);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        //when
        Optional<User> findUser = userService.findById(1L);

        //then
        assertTrue(findUser.isPresent());
        assertThat(findUser.get().getBalance()).isEqualTo(10_000);

    }

    @Test
    void 충전_반영() {

        //given
        long userId = 1L;
        int wantToCharge = 10_000;
        User user = new User(userId,"JHC");
        user.addAmount(wantToCharge);
        given(userRepository.save(user)).willReturn(user);

        //when
        User charge = userService.charge(user);

        //then
        assertThat(charge.getBalance()).isEqualTo(wantToCharge);

    }

    @Test
    void 충전_이력_저장() {
        //given
        long userId = 1L;
        int wantToCharge = 10_000;
        User user = new User(userId,"JHC");
        user.addAmount(wantToCharge);

        BalanceHistory balanceHistory = new BalanceHistory(user, wantToCharge, TransactionType.CHARGE);
        given(balanceHistoryRepository.save(balanceHistory)).willReturn(balanceHistory);

        //when
        BalanceHistory savedBalanceHistory = userService.saveHistory(balanceHistory);

        assertThat(savedBalanceHistory.getType()).isEqualTo(TransactionType.CHARGE);
    }

    @Test
    void 사용_이력_저장() {

        //given
        long userId = 1L;
        int have = 100_000;
        int wantToUse = 10_000;
        User user = new User(userId,"JHC");
        user.addAmount(have);
        user.subtractAmount(wantToUse);

        BalanceHistory balanceHistory = new BalanceHistory(user, wantToUse, TransactionType.USE);
        given(balanceHistoryRepository.save(balanceHistory)).willReturn(balanceHistory);


        //when
        BalanceHistory savedBalanceHistory = userService.saveHistory(balanceHistory);

        assertThat(savedBalanceHistory.getType()).isEqualTo(TransactionType.USE);


    }
}
