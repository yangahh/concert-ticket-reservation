package kr.hhplus.be.server.interfaces.api.queuetoken.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.queuetoken.dto.QueueTokenPositionResult;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QueuePositionResponse {
    @Schema(description = "대기열 순번", example = "123")
    private int position;

    @Schema(description = "토큰 활성 여부", example = "false")
    private boolean isActive;

    @Schema(description = "진입하기까지 남은 시간(초)", example = "12345")
    private int remainingSeconds;

    public static QueuePositionResponse fromDomainDto(QueueTokenPositionResult dto) {
        return QueuePositionResponse.builder()
                .position(dto.position())
                .isActive(dto.isActive())
                .remainingSeconds(dto.remainingSeconds())
                .build();
    }
}
