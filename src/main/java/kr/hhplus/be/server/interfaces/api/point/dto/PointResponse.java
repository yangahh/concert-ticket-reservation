package kr.hhplus.be.server.interfaces.api.point.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.point.dto.PointResult;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PointResponse {
    @Schema(description = "사용자 ID", example = "1")
    private long userId;

    @Schema(description = "포인트 잔액", example = "105000" )
    private Integer balance;

    public static PointResponse fromDomainDto(PointResult dto) {
        return PointResponse.builder()
                .userId(dto.userId())
                .balance(dto.balance())
                .build();
    }
}
