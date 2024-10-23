package io.hhplus.javaconcerthancil.interfaces.api.v1.user;

import io.hhplus.javaconcerthancil.application.user.UserFacade;
import io.hhplus.javaconcerthancil.interfaces.api.common.ApiResponse;
import io.hhplus.javaconcerthancil.interfaces.api.v1.user.request.ChargeRequest;
import io.hhplus.javaconcerthancil.interfaces.api.v1.user.response.ChargeResponse;
import io.hhplus.javaconcerthancil.interfaces.api.v1.user.response.UserBalanceResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

@Tag(   name        =   "04_사용자 잔액 충전 및 조회 API",
        description =   "")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/balance")
@Log4j2
public class UserController {

    private final UserFacade userFacade;


    @PatchMapping("/{userId}/charge")
    public ApiResponse<ChargeResponse> charge(
            @PathVariable("userId") Long userId,
            @RequestBody ChargeRequest requestBody
    ){
        log.info("requestBody: {}", requestBody);
        return ApiResponse.success(userFacade.charge(userId, requestBody));
    }


    @GetMapping("/{userId}")
    public ApiResponse<UserBalanceResponse> getUserBalance(
            @PathVariable("userId") Long userId
    ){
        return ApiResponse.success(userFacade.getUserBalance(userId));
    }


}
