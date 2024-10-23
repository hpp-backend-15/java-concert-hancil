package io.hhplus.javaconcerthancil.interfaces.api.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public class ApiResponse<T> {

    @JsonProperty("errorResponse")
    private ErrorResponse errorResponse;

    @JsonProperty("data")
    private T data;

    private ApiResponse(T data) {
        this.data = data;
    }

    private ApiResponse(ErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data);
    }

    public static ApiResponse<?> error(ErrorCode error, Object errorData) {
        return new ApiResponse<>(new ErrorResponse(error.getCode(), error.getMessage(), errorData));
    }

    public static ApiResponse<?> error(String message) {
        return new ApiResponse<>(new ErrorResponse("500", message, null));
    }
}
