package kr.hhplus.be.server.domain.queuetoken.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.utils.time.TimeProvider;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "queue_token",
        indexes = {
                @Index(name = "idx_queue_token_user_id", columnList = "user_id"),
                @Index(name = "idx_queue_token_concert_id", columnList = "concert_id")
})
public class QueueToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "token_uuid", unique = true, nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    private UUID tokenUuid;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull
    @Column(name = "concert_id", nullable = false)
    private Long concertId;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP(6)")
    private LocalDateTime createdAt;

    @Column(name = "expired_at", updatable = false, columnDefinition = "TIMESTAMP(6)")
    private LocalDateTime expiredAt;

    @Builder(access = AccessLevel.PROTECTED)
    QueueToken(UUID tokenUuid, Long userId, Long concertId, boolean isActive, LocalDateTime createdAt, LocalDateTime expiredAt) {
        this.tokenUuid = tokenUuid;
        this.userId = userId;
        this.concertId = concertId;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
    }

    public static QueueToken createWaitingToken(Long userId, Long concertId, TimeProvider timeProvider) {
        LocalDateTime now = timeProvider.now();
        return QueueToken.builder()
                .tokenUuid(UUID.randomUUID())
                .userId(userId)
                .concertId(concertId)
                .isActive(false)
                .createdAt(now)
                .expiredAt(now.plusMinutes(10))
                .build();
    }

    public static QueueToken fromRedis(Long concertId, UUID tokenUuid, boolean isActive) {
        return QueueToken.builder()
            .tokenUuid(tokenUuid)
            .concertId(concertId)
            .isActive(isActive)
            .build();
    }

    public void activate() {
        if (!isActive) {
            this.isActive = true;
        }
    }

    public boolean isExpired(TimeProvider timeProvider) {
        if (expiredAt == null) return false;  // Redis에서 가져온 경우 만료된 경우는 Redis에 없기 때문에 항상 isExpired가 false가 됨
        return timeProvider.now().isAfter(expiredAt);
    }

    public boolean isValid(TimeProvider timeProvider) {
        return isActive && !isExpired(timeProvider);
    }
}
