package io.hhplus.javaconcerthancil.interfaces.api.common;

import lombok.Getter;
import org.springframework.boot.logging.LogLevel;

@Getter
public class ApiException extends RuntimeException {

    private final ErrorCode errorCode;

    private final LogLevel logLevel;

    private final Object payload;

    public ApiException(ErrorCode errorCode, LogLevel logLevel) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.logLevel = logLevel;
        this.payload = null;
    }

    public ApiException(ErrorCode errorCode, LogLevel logLevel, Object payload) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.logLevel = logLevel;
        this.payload = payload;
    }

}
