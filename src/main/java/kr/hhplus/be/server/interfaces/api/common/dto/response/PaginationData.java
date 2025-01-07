package kr.hhplus.be.server.interfaces.api.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PaginationData<T> {
    @Schema(description = "페이지 시작 인덱스", example = "0")
    private final Integer offset;

    @Schema(description = "페이지 당 조회 개수", example = "10")
    private final Integer limit;

    @Schema(description = "전체 데이터 개수", example = "100")
    private final Long count;
    private final List<T> items;
}
