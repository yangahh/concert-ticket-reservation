package kr.hhplus.be.server.interfaces.api.queuetoken.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.queuetoken.dto.QueueTokenResult;
import kr.hhplus.be.server.interfaces.utils.queuetoken.QueueTokenEncoder;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QueueTokenResponse {
    @Schema(description = "대기열 토큰 uuid와 concertId를 base64로 인코딩한 값", example = "MTU3YWFkZmEtOTZiYi00YjFhLTgxMjctZDZlM2RkMzFkNzJiOmFiYw==")
    private String token;

    @Schema(description = "토큰 활성 여부", example = "false")
    private boolean isActive;

    public static QueueTokenResponse fromDomainDto(QueueTokenResult dto) {
        return QueueTokenResponse.builder()
                .token(QueueTokenEncoder.base64EncodeToken(dto.tokenUuid(), dto.concertId()))
                .isActive(dto.isActive())
                .build();
    }
}
