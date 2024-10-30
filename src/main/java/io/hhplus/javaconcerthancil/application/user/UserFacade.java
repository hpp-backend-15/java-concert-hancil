package io.hhplus.javaconcerthancil.application.user;

import io.hhplus.javaconcerthancil.domain.user.*;
import io.hhplus.javaconcerthancil.interfaces.api.common.ApiException;
import io.hhplus.javaconcerthancil.interfaces.api.common.ErrorCode;
import io.hhplus.javaconcerthancil.interfaces.api.v1.user.request.ChargeRequest;
import io.hhplus.javaconcerthancil.interfaces.api.v1.user.response.ChargeResponse;
import io.hhplus.javaconcerthancil.interfaces.api.v1.user.response.UserBalanceResponse;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.logging.LogLevel;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
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
//        BalanceHistory balanceHistory = new BalanceHistory(
//            user, requestBody.amount(), TransactionType.CHARGE
//        );
//        userService.saveHistory(balanceHistory);

        return new ChargeResponse(balanceUpdatedUser.getId(), balanceUpdatedUser.getBalance());
    }

    @Transactional
    public ChargeResponse chargeWithPessimisticLock(Long userId, ChargeRequest requestBody) {
        //1. 사용자 조회
        User user = userService.findByIdWithLock(userId).orElseThrow(() ->
                new ApiException(ErrorCode.E404, LogLevel.INFO, "사용자가 존재하지 않습니다.")
        );

        //2. 충전 및 저장
        user.addAmount(requestBody.amount());
        User balanceUpdatedUser = userService.updateBalance(user);

        //3. 이력 저장
//        BalanceHistory balanceHistory = new BalanceHistory(
//            user, requestBody.amount(), TransactionType.CHARGE
//        );
//        userService.saveHistory(balanceHistory);

        return new ChargeResponse(balanceUpdatedUser.getId(), balanceUpdatedUser.getBalance());
    }

    public UserBalanceResponse getUserBalance(Long userId) {

        User user = userService.findById(userId).orElseThrow(()->
                new ApiException(ErrorCode.E404, LogLevel.INFO, "사용자가 존재하지 않습니다.")
        );

        return new UserBalanceResponse(user.getId(), user.getBalance());
    }

    @Transactional
//    @Retryable(
//            retryFor = {OptimisticLockingFailureException.class, ObjectOptimisticLockingFailureException.class},
//            maxAttempts = 5,
//            backoff = @Backoff(delay = 1000)
//    )
    public void chargeWithOptimisticLock(long userId, ChargeRequest requestBody) {

        //1. 사용자 조회
        UserWithVersion user = userService.findByIdWithVersion(userId).orElseThrow(() ->
                new ApiException(ErrorCode.E404, LogLevel.INFO, "사용자가 존재하지 않습니다.")
        );

        //2. 충전 및 저장
        user.addAmount(requestBody.amount());
        userService.updateBalanceWithVersion(user);

//        try {
            // 낙관적 락이 걸린 상태에서 저장
//        } catch (ObjectOptimisticLockingFailureException e) {
//            throw new ApiException(ErrorCode.E409, LogLevel.INFO, "충돌이 발생했습니다. 다시 시도해주세요.");
//        }


        //3. 이력 저장
//        BalanceHistory balanceHistory = new BalanceHistory(
//            user, requestBody.amount(), TransactionType.CHARGE
//        );
//        userService.saveHistory(balanceHistory);

    }
}
