package kr.hhplus.be.server.interfaces.api.common.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {
    private final int statusCode;
    private final String message;
    private final T data;

    @Builder
    private BaseResponse(int statusCode, String message, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public static <T> BaseResponse<T> ok(T data) {
        return BaseResponse.<T>builder()
                .statusCode(HttpStatus.OK.value())
                .message(HttpStatus.OK.name())
                .data(data)
                .build();
    }

    public static <T> BaseResponse<T> ok(String message, T data) {
        return BaseResponse.<T>builder()
                .statusCode(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .build();
    }

    public static <T> BaseResponse<T> created() {
        return BaseResponse.<T>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message(HttpStatus.CREATED.name())
                .build();
    }

    public static <T> BaseResponse<T> created(T data) {
        return BaseResponse.<T>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message(HttpStatus.CREATED.name())
                .data(data)
                .build();
    }
}
