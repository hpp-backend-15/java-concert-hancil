package io.hhplus.javaconcerthancil.unit.domain.user;

import io.hhplus.javaconcerthancil.domain.user.BalanceHistory;
import io.hhplus.javaconcerthancil.domain.user.TransactionType;
import io.hhplus.javaconcerthancil.domain.user.User;
import io.hhplus.javaconcerthancil.interfaces.api.common.ApiException;
import io.hhplus.javaconcerthancil.interfaces.api.common.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


public class UserUnitTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("JHC",0);
    }

    @Test
    void 사용자_조회() {
        assertThat(user.getName()).isEqualTo("JHC");
        assertThat(user.getBalance()).isEqualTo(0);
        assertThat(user.getBalanceHistoryList()).isEmpty();
    }

    @Test
    void 사용자는_원하는금액만큼_충전한다() {

        //when
        final int wantToCharge = 10_000;
        user.addAmount(wantToCharge);

        //then
        assertThat(user.getBalance()).isEqualTo(wantToCharge);
    }


    @Test
    void 충전시_충전_이력이_저장되는지() {

        //when
        final int wantToCharge = 10_000;
        user.addAmount(wantToCharge);

        //then
        assertThat(user.getBalanceHistoryList()).hasSize(1);

        BalanceHistory balanceHistory = user.getBalanceHistoryList().get(0);
        assertThat(balanceHistory.getAmount()).isEqualTo(wantToCharge);
        assertThat(balanceHistory.getType()).isEqualTo(TransactionType.CHARGE);
    }

    @Test
    void 사용자는_결제하면_금액이_차감됩니다() {

        //given
        //10만포인트가 있어요
        final int userHave = 100_000;
        final int wantToUse = 10_000;
        User user = new User("RICH",userHave);

        //when
        user.subtractAmount(wantToUse);

        //then
        assertThat(user.getBalance()).isEqualTo(userHave - wantToUse);

        assertThat(user.getBalanceHistoryList()).hasSize(1);

        BalanceHistory balanceHistory = user.getBalanceHistoryList().get(0);
        assertThat(balanceHistory.getAmount()).isEqualTo(wantToUse);
        assertThat(balanceHistory.getType()).isEqualTo(TransactionType.USE);


    }

    @Test
    void 충전금액은_양수여야합니다() {
        assertThatThrownBy(()->
                user.addAmount(0))
                .isInstanceOf(ApiException.class)
                .hasMessage(ErrorCode.E006.getMessage());

    }

    @Test
    void 사용자가_잔액을_사용할_때_잔액이_부족할_경우_예외_발생(){
        // given
        assertThatThrownBy(() ->  user.subtractAmount(10_000))
                .isInstanceOf(ApiException.class)
                .hasMessage(ErrorCode.E005.getMessage());
    }
}
