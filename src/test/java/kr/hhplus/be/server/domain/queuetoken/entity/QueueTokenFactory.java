package kr.hhplus.be.server.domain.queuetoken.entity;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

public class QueueTokenFactory {
    public static QueueToken createMock(UUID tokenUuid, long userId, long concertId, boolean isActive, Clock clock) {
        LocalDateTime createdAt = LocalDateTime.now(clock);
        return QueueToken.builder()
            .tokenUuid(tokenUuid)
            .userId(userId)
            .concertId(concertId)
            .isActive(isActive)
            .createdAt(createdAt)
            .expiredAt(createdAt.plusMinutes(5))
            .build();
    }

    public static QueueToken createMock(UUID tokenUuid, long userId, long concertId, boolean isActive) {
        return QueueToken.builder()
            .tokenUuid(tokenUuid)
            .userId(userId)
            .concertId(concertId)
            .isActive(isActive)
            .createdAt(LocalDateTime.now())
            .expiredAt(LocalDateTime.now().plusMinutes(5))
            .build();
    }
}
