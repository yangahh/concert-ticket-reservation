package kr.hhplus.be.server.interfaces.api.queuetoken.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QueuePositionResponse {
    private long position;
    private boolean isActive;

    public static QueuePositionResponse of(long position, boolean isActive) {
        return QueuePositionResponse.builder()
                .position(position)
                .isActive(isActive)
                .build();
    }
}
