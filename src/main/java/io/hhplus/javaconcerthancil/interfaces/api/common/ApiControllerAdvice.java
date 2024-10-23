package io.hhplus.javaconcerthancil.interfaces.api.common;


import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@RestControllerAdvice
//@Slf4j
public class ApiControllerAdvice extends ResponseEntityExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());

    // 내가 정의한 Exception 이 발생했을 때 에러 응답
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
