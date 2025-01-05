package kr.hhplus.be.server.interfaces.api.queuetoken.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QueueTokenResponse {
    private String token;
    private boolean isActive;

    public static QueueTokenResponse of(String token, boolean isActive) {
        return QueueTokenResponse.builder()
                .token(token)
                .isActive(isActive)
                .build();
    }
}
