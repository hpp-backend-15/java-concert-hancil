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


    @PatchMapping("/{userId}/charge")
    @Override
    public ApiResponse<ChargeResponse> charge(Long userId, ChargeRequest requestBody) {
        log.info("requestBody: {}", requestBody);
        return ApiResponse.success(userFacade.charge(userId, requestBody));
    }

    @PatchMapping("/{userId}/chargeWithOptimisticLock")
    @Override
    public ApiResponse<ChargeResponse> chargeWithOptimisticLock(Long userId, ChargeRequest requestBody) {
        log.info("requestBody: {}", requestBody);
        return ApiResponse.success(userFacade.chargeWithOptimisticLock(userId, requestBody));
    }


    @PatchMapping("/{userId}/chargeWithPessimisticLock")
    @Override
    public ApiResponse<ChargeResponse> chargeWithPessimisticLock(Long userId, ChargeRequest requestBody) {
        log.info("requestBody: {}", requestBody);
        return ApiResponse.success(userFacade.chargeWithPessimisticLock(userId, requestBody));
    }


    @Retryable(
            retryFor = { InterruptedException.class }, // 리트라이할 예외 클래스
            maxAttempts = 10, // 최대 재시도 횟수
            backoff = @Backoff(delay = 100) // 지연 시간 설정 (100ms)
    )
    @Override
    public ApiResponse<ChargeResponse> chargeWithRedisLock(Long userId, ChargeRequest requestBody){
        log.info("requestBody: {}", requestBody);

        RLock lock = redissonClient.getLock("lock:user:" + userId);
        ChargeResponse charge = null;

        try {
            // 락 획득 시도
            if (lock.tryLock(100, 100, TimeUnit.MILLISECONDS)) {
                charge = userFacade.charge(userId, requestBody);
            } else {
                log.info("이미 처리 중인 요청입니다. 락을 획득할 수 없습니다.");
            }
        } catch (InterruptedException e) {
            throw new ApiException(ErrorCode.E409, LogLevel.ERROR);
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock(); // 락 해제
            }
        }

        return ApiResponse.success(charge);
    }

    @Override
    @GetMapping("/{userId}")
    public ApiResponse<UserBalanceResponse> getUserBalance(Long userId) {
        return ApiResponse.success(userFacade.getUserBalance(userId));
    }


//    @PatchMapping("/{userId}/charge")
//    public ApiResponse<ChargeResponse> charge(
//            @PathVariable("userId") Long userId,
//            @RequestBody ChargeRequest requestBody
//    ){
//        log.info("requestBody: {}", requestBody);
//        return ApiResponse.success(userFacade.charge(userId, requestBody));
//    }
//
//
//    @GetMapping("/{userId}")
//    public ApiResponse<UserBalanceResponse> getUserBalance(
//            @PathVariable("userId") Long userId
//    ){
//        return ApiResponse.success(userFacade.getUserBalance(userId));
//    }


}
