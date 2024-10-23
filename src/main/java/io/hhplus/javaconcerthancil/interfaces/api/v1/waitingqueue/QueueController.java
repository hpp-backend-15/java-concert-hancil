package io.hhplus.javaconcerthancil.interfaces.api.v1.waitingqueue;

import io.hhplus.javaconcerthancil.application.waitingqueue.WaitingQueueFacade;
import io.hhplus.javaconcerthancil.interfaces.api.common.ApiResponse;
import io.hhplus.javaconcerthancil.interfaces.api.v1.waitingqueue.request.QueueStatusRequest;
import io.hhplus.javaconcerthancil.interfaces.api.v1.waitingqueue.response.IssueTokenResponse;
import io.hhplus.javaconcerthancil.interfaces.api.v1.waitingqueue.response.QueueStatusResponse;
import io.hhplus.javaconcerthancil.interfaces.api.v1.waitingqueue.request.CreateQueueTokenRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(   name        = "01_대기열 API",
        description = "대기열 토큰 발급 및 대기열 정보를 조회합니다.")
@RestController
@RequestMapping("/v1/queue")
@Log4j2
@RequiredArgsConstructor
public class QueueController {
    private final static LocalDateTime NOW = LocalDateTime.now();
    private final WaitingQueueFacade queueFacade;

    @PostMapping("/issue-token")
    public ApiResponse<IssueTokenResponse> createQueueToken(
            @RequestBody CreateQueueTokenRequest requestBody
    ){
        return ApiResponse.success(queueFacade.issueToken(requestBody.userId()));

    }

    @PostMapping("/status")
    public ApiResponse<QueueStatusResponse> getQueueStatus(HttpServletRequest request,
                                              @RequestBody QueueStatusRequest requestBody
    ){
        String token = request.getHeader("QUEUE_TOKEN");
        log.info("token: {}", token);
        log.info("request: {}", requestBody);

        return ApiResponse.success(queueFacade.getTokenInfo(token));
    }
}
