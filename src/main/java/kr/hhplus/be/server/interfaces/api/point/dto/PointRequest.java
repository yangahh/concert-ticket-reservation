package kr.hhplus.be.server.interfaces.api.point.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointRequest {
    @Schema(description = "사용자 ID", example = "1")
    @NotNull
    @Positive
    private Long userId;

    @Schema(description = "충전할 포인트 금액", example = "5000")
    @NotNull
    @Positive
    private Long amount;
}
