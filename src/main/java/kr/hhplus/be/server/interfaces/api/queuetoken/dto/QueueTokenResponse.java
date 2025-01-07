package kr.hhplus.be.server.interfaces.api.queuetoken.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QueueTokenResponse {
    @Schema(description = "발급된 대기열 토큰(uuid)", example = "157aadfa-96bb-4b1a-8127-d6e3dd31d72b")
    private String token;

    @Schema(description = "토큰 활성 여부", example = "false")
    private boolean isActive;

    public static QueueTokenResponse of(String token, boolean isActive) {
        return QueueTokenResponse.builder()
                .token(token)
                .isActive(isActive)
                .build();
    }
}
