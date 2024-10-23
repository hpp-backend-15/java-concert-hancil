package io.hhplus.javaconcerthancil.application.user;

import io.hhplus.javaconcerthancil.domain.user.BalanceHistory;
import io.hhplus.javaconcerthancil.domain.user.TransactionType;
import io.hhplus.javaconcerthancil.domain.user.User;
import io.hhplus.javaconcerthancil.domain.user.UserService;
import io.hhplus.javaconcerthancil.interfaces.api.common.ApiException;
import io.hhplus.javaconcerthancil.interfaces.api.common.ErrorCode;
import io.hhplus.javaconcerthancil.interfaces.api.v1.user.request.ChargeRequest;
import io.hhplus.javaconcerthancil.interfaces.api.v1.user.response.ChargeResponse;
import io.hhplus.javaconcerthancil.interfaces.api.v1.user.response.UserBalanceResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;

    @Transactional
    public ChargeResponse charge(Long userId, ChargeRequest requestBody) {

        //1. 사용자 조회
        User user = userService.findById(userId).orElseThrow(() ->
                new ApiException(ErrorCode.E404, LogLevel.INFO, "사용자가 존재하지 않습니다.")
        );

        //2. 충전 및 저장
        user.addAmount(requestBody.amount());
        User balanceUpdatedUser = userService.updateBalance(user);

        //3. 이력 저장
        BalanceHistory balanceHistory = new BalanceHistory(
            user, requestBody.amount(), TransactionType.CHARGE
        );
        userService.saveHistory(balanceHistory);

        return new ChargeResponse(balanceUpdatedUser.getId(), balanceUpdatedUser.getBalance());
    }

    public UserBalanceResponse getUserBalance(Long userId) {

        User user = userService.findById(userId).orElseThrow(()->
                new ApiException(ErrorCode.E404, LogLevel.INFO, "사용자가 존재하지 않습니다.")
        );

        return new UserBalanceResponse(user.getId(), user.getBalance());
    }
}
