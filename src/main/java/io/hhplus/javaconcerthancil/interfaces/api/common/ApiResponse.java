package io.hhplus.javaconcerthancil.interfaces.api.common;

public class ApiResponse<T> {

    private T data;

    public ApiResponse(T data) {
        this.data = data;
    }


    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data);
    }

    public static ApiResponse<?> error(ErrorCode error, Object errorData) {
        return new ApiResponse<>(new ErrorResponse(error.getCode(), error.getMessage(), errorData));
    }

    public static Object error(String message) {
        return new ApiResponse<>(new ErrorResponse("500", message, null));
    }
}
