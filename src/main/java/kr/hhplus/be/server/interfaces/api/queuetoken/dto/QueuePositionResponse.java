package kr.hhplus.be.server.interfaces.api.queuetoken.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QueuePositionResponse {
    @Schema(description = "대기열 순번", example = "123")
    private long position;

    @Schema(description = "토큰 활성 여부", example = "false")
    private boolean isActive;

    public static QueuePositionResponse of(long position, boolean isActive) {
        return QueuePositionResponse.builder()
                .position(position)
                .isActive(isActive)
                .build();
    }
}
