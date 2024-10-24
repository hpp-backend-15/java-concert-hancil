package io.hhplus.javaconcerthancil.interfaces.api.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@RestControllerAdvice
@Slf4j
public class ApiControllerAdvice extends ResponseEntityExceptionHandler {

    // 내가 정의한 Exception 이 발생했을 때 에러 응답
    // default: 에러 핸들러를 통해 예외 로깅 및 응답 처리 핸들러 구현
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<?>> handleCustomException(ApiException e) {
        switch (e.getLogLevel()) {
            case ERROR -> log.error("ApiException : {}", e.getMessage(), e);
            case WARN -> log.warn("ApiException : {}", e.getMessage(), e);
            default -> log.info("ApiException : {}", e.getMessage(), e);
        }
        // Http status 200 선호 "UserNotFound --> x 200"
        return new ResponseEntity<>(ApiResponse.error(e.getErrorCode(), e.getPayload()), OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        log.error("UnhandledException : {}", e.getMessage(), e);
        return new ResponseEntity<>(ApiResponse.error(e.getMessage()), INTERNAL_SERVER_ERROR);
    }
}
