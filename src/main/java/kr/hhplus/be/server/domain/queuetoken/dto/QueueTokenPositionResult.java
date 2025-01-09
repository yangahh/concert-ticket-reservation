package kr.hhplus.be.server.domain.queuetoken.dto;

import kr.hhplus.be.server.domain.queuetoken.entity.QueueToken;
import lombok.Builder;

import java.util.UUID;

@Builder
public record QueueTokenPositionResult (
        UUID tokenUuid,
        long userId,
        long concertId,
        int position,
        int remainingSeconds,
        boolean isActive
) {
    public static QueueTokenPositionResult fromEntity(QueueToken entity, int position, int remainingSeconds) {
        return QueueTokenPositionResult.builder()
                .tokenUuid(entity.getTokenUuid())
                .userId(entity.getUserId())
                .concertId(entity.getConcertId())
                .position(position)
                .remainingSeconds(remainingSeconds)
                .isActive(entity.isActive())
                .build();
    }
}
