package kr.hhplus.be.server.interfaces.api.common.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@NoArgsConstructor
public class PaginationResponse {
    public static <T> BaseResponse<PaginationData<T>> of(List<T> items, Integer offset, Integer limit, Long count) {
        PaginationData<T> paginationData = new PaginationData<>(offset, limit, count, items);

        return BaseResponse.<PaginationData<T>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(HttpStatus.OK.name())
                .data(paginationData)
                .build();
    }
}