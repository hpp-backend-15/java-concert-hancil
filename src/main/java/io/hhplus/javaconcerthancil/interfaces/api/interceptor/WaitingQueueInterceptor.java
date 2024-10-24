package io.hhplus.javaconcerthancil.interfaces.api.interceptor;

import io.hhplus.javaconcerthancil.domain.waitingqueue.WaitingQueueService;
import io.hhplus.javaconcerthancil.interfaces.api.common.ApiException;
import io.hhplus.javaconcerthancil.interfaces.api.common.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class WaitingQueueInterceptor implements HandlerInterceptor {

    private final WaitingQueueService queueService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //1. 토큰 만료 검증
        String token = request.getHeader("QUEUE_TOKEN");
        boolean isActive = queueService.isActiveToken(token);

        //2. 토큰이 없거나 비활성화된 상태면 에러
        if (!isActive){
            throw new ApiException(ErrorCode.E003, LogLevel.INFO, "invalid token");
        }

        return true;
    }

}
