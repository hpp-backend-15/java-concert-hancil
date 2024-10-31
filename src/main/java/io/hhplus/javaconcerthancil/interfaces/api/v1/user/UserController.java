package io.hhplus.javaconcerthancil.interfaces.api.v1.user;

import io.hhplus.javaconcerthancil.application.user.UserFacade;
import io.hhplus.javaconcerthancil.interfaces.api.common.ApiException;
import io.hhplus.javaconcerthancil.interfaces.api.common.ApiResponse;
import io.hhplus.javaconcerthancil.interfaces.api.common.ErrorCode;
import io.hhplus.javaconcerthancil.interfaces.api.v1.user.request.ChargeRequest;
import io.hhplus.javaconcerthancil.interfaces.api.v1.user.response.ChargeResponse;
import io.hhplus.javaconcerthancil.interfaces.api.v1.user.response.UserBalanceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/balance")
@Log4j2
public class UserController implements IUserController {

    private final UserFacade userFacade;
    private final RedissonClient redissonClient;

    @Override
    public ApiResponse<ChargeResponse> charge(Long userId, ChargeRequest requestBody) {
        log.info("requestBody: {}", requestBody);
        return ApiResponse.success(userFacade.charge(userId, requestBody));
    }

    @Override
    public ApiResponse<ChargeResponse> chargeWithOptimisticLock(Long userId, ChargeRequest requestBody) {
        log.info("requestBody: {}", requestBody);
        return ApiResponse.success(userFacade.chargeWithOptimisticLock(userId, requestBody));
    }


    @Override
    public ApiResponse<ChargeResponse> chargeWithPessimisticLock(Long userId, ChargeRequest requestBody) {
        log.info("requestBody: {}", requestBody);
        return ApiResponse.success(userFacade.chargeWithPessimisticLock(userId, requestBody));
    }


    @Retryable(
            retryFor = { ApiException.class }, // 리트라이할 예외 클래스
            maxAttempts = 10 // 최대 재시도 횟수
    )
    @Override
    public ApiResponse<ChargeResponse> chargeWithRedisLock(Long userId, ChargeRequest requestBody){
        log.info("requestBody: {}", requestBody);

        RLock lock = redissonClient.getLock("lock:user:" + userId);
        ChargeResponse charge = null;

        try {
            // 락 획득 시도
            if (lock.tryLock(500, 100, TimeUnit.MILLISECONDS)) {
                charge = userFacade.charge(userId, requestBody);
            } else {
                log.info("이미 처리 중인 요청입니다. 락을 획득할 수 없습니다.");
                throw new ApiException(ErrorCode.E409, LogLevel.ERROR, "락 획득 실패로 인한 재시도");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException(ErrorCode.E500, LogLevel.ERROR, "스레드 인터럽트 발생");
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock(); // 락 해제
            }
        }

        return ApiResponse.success(charge);
    }

    @Override
    public ApiResponse<UserBalanceResponse> getUserBalance(Long userId) {
        return ApiResponse.success(userFacade.getUserBalance(userId));
    }

}
