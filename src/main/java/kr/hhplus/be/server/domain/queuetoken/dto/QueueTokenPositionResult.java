package kr.hhplus.be.server.domain.queuetoken.dto;

import kr.hhplus.be.server.domain.queuetoken.entity.QueueToken;
import lombok.Builder;

import java.util.UUID;

@Builder
public record QueueTokenPositionResult (
        UUID tokenUuid,
        Long userId,
        Long concertId,
        Integer position,
        Integer remainingSeconds,
        Boolean isActive
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
