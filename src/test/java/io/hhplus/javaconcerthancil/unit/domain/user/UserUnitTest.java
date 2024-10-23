package io.hhplus.javaconcerthancil.unit.domain.user;

import io.hhplus.javaconcerthancil.domain.user.User;
import io.hhplus.javaconcerthancil.interfaces.api.common.ApiException;
import io.hhplus.javaconcerthancil.interfaces.api.common.ErrorCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class UserUnitTest {

    @Test
    void 사용자는_원하는금액만큼_충전한다() {

        //given
        User user = new User("JHC");
        final int wantToCharge = 10_000;

        //when
        user.addAmount(wantToCharge);

        //then
        assertThat(user.getBalance()).isEqualTo(10_000);

    }

    @Test
    void 사용자는_결제하면_금액이_차감됩니다() {

        //given
        //10만포인트가 있어요
        int userHave = 100_000;
        User user = new User("JHC");
        user.addAmount(userHave);


        //when
        user.subtractAmount(10_000);

        //then
        assertThat(user.getBalance()).isEqualTo(userHave- 10_000);
    }

    @Test
    void 충전금액은_양수여야합니다() {
        //given
        User user = new User("JHC");


        assertThatThrownBy(()->
                user.addAmount(0))
                .isInstanceOf(ApiException.class)
                .hasMessage(ErrorCode.E006.getMessage());

    }

    @Test
    void 사용자가_잔액을_사용할_때_잔액이_부족할_경우_예외_발생(){
        // given
        User user = new User("JHC");

        assertThatThrownBy(() ->  user.subtractAmount(10_000))
                .isInstanceOf(ApiException.class)
                .hasMessage(ErrorCode.E005.getMessage());
    }
}
