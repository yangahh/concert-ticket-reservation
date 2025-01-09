package kr.hhplus.be.server.domain.queuetoken.dto;

import kr.hhplus.be.server.domain.queuetoken.entity.QueueToken;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record QueueTokenResult(
        UUID tokenUuid,
        Long userId,
        Long concertId,
        boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime expiredAt
) {
    public static QueueTokenResult fromEntity(QueueToken entity) {
        return QueueTokenResult.builder()
                .tokenUuid(entity.getTokenUuid())
                .userId(entity.getUserId())
                .concertId(entity.getConcertId())
                .isActive(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .expiredAt(entity.getExpiredAt())
                .build();

    }
}
