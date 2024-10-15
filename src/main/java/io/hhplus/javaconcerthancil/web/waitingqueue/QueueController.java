package io.hhplus.javaconcerthancil.web.waitingqueue;

import io.hhplus.javaconcerthancil.web.waitingqueue.request.QueueStatusRequest;
import io.hhplus.javaconcerthancil.web.waitingqueue.response.IssueTokenResponse;
import io.hhplus.javaconcerthancil.web.waitingqueue.response.QueueStatusResponse;
import io.hhplus.javaconcerthancil.domain.waitingqueue.QueueStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(   name        = "대기열 API",
        description = "대기열 토큰 발급 및 대기열 정보를 조회합니다.")
@RestController
@RequestMapping("/v1/queue")
@Log4j2
public class QueueController {
    private final static LocalDateTime NOW = LocalDateTime.now();

    @PostMapping("/issue-token")
    public IssueTokenResponse createQueueToken(){
        return new IssueTokenResponse(1L, NOW, NOW.plusMinutes(10));
    }

    @PostMapping("/status")
    public QueueStatusResponse getQueueStatus(
            HttpServletRequest request,
            @RequestBody QueueStatusRequest requestBody
    ){
        log.info("token: {}", request.getHeader("Authorization"));
        log.info("request: {}", requestBody);
        return new QueueStatusResponse(10L, 1L, NOW.plusMinutes(10), QueueStatus.WAITING);
    }
}
