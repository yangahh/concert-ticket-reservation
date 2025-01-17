package kr.hhplus.be.server.testfactory.domain.queuetoken.entity;

import kr.hhplus.be.server.domain.queuetoken.entity.QueueToken;

import java.time.LocalDateTime;
import java.util.UUID;

public class QueueTokenFactory {
    public static Builder builder(long userId, long concertId) {
        return new Builder(userId, concertId);
    }

    public static class Builder {
        private final long userId;
        private final long concertId;
        private UUID tokenUuid = UUID.randomUUID(); // 기본값
        private boolean isActive = true; // 기본값
        private LocalDateTime createdAt = LocalDateTime.now(); // 기본값
        private LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(30); // 기본값

        private Builder(long userId, long concertId) {
            this.userId = userId;
            this.concertId = concertId;
        }

        public Builder tokenUuid(UUID tokenUuid) {
            this.tokenUuid = tokenUuid;
            return this;
        }

        public Builder isActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder expiredAt(LocalDateTime expiredAt) {
            this.expiredAt = expiredAt;
            return this;
        }

        public QueueToken build() {
            return QueueToken.builder()
                .userId(userId)
                .concertId(concertId)
                .tokenUuid(tokenUuid)
                .isActive(isActive)
                .createdAt(createdAt)
                .expiredAt(expiredAt)
                .build();
        }
    }
}
