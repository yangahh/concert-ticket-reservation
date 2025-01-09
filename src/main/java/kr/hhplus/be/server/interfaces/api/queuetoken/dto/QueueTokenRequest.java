package kr.hhplus.be.server.interfaces.api.queuetoken.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueueTokenRequest {
    @Schema(description = "사용자 ID", example = "1")
    @NotNull(message = "userId는 필수값입니다.")
    @Positive
    private Long userId;

    @Schema(description = "콘서트 ID", example = "101")
    @NotNull(message = "concertId는 필수값입니다.")
    @Positive
    private Long concertId;
}
