package io.hhplus.javaconcerthancil.interfaces.api.v1.user;

import io.hhplus.javaconcerthancil.interfaces.api.common.ApiResponse;
import io.hhplus.javaconcerthancil.interfaces.api.v1.user.request.ChargeRequest;
import io.hhplus.javaconcerthancil.interfaces.api.v1.user.response.ChargeResponse;
import io.hhplus.javaconcerthancil.interfaces.api.v1.user.response.UserBalanceResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(   name        =   "04_사용자 잔액 충전 및 조회 API",
        description =   "")
public interface IUserController {

//    @PatchMapping("/{userId}/charge")
    ApiResponse<ChargeResponse> charge(
            @PathVariable("userId") Long userId,
            @RequestBody ChargeRequest requestBody
    );

//    @PatchMapping("/{userId}/chargeWithOptimisticLock")
    @PatchMapping("/{userId}/charge")
    ApiResponse<ChargeResponse> chargeWithOptimisticLock(
            @PathVariable("userId") Long userId,
            @RequestBody ChargeRequest requestBody);

//    @PatchMapping("/{userId}/chargeWithPessimisticLock")
    ApiResponse<ChargeResponse> chargeWithPessimisticLock(
            @PathVariable("userId") Long userId,
            @RequestBody ChargeRequest requestBody);

//    @PatchMapping("/{userId}/chargeWithRedisLock")
    ApiResponse<ChargeResponse> chargeWithRedisLock(
            @PathVariable("userId") Long userId,
            @RequestBody ChargeRequest requestBody) throws InterruptedException;

    @GetMapping("/{userId}")
    ApiResponse<UserBalanceResponse> getUserBalance(
            @PathVariable("userId") Long userId
    );


}
