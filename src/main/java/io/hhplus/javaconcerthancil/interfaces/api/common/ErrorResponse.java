package io.hhplus.javaconcerthancil.interfaces.api.common;

public record ErrorResponse(
        String code,
        String message,
        Object data
) {
}
