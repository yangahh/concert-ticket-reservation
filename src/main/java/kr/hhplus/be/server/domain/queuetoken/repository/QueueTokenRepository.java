package kr.hhplus.be.server.domain.queuetoken.repository;

import kr.hhplus.be.server.domain.queuetoken.entity.QueueToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QueueTokenRepository {
    QueueToken save(QueueToken queueToken);

    Optional<QueueToken> findByTokenUuid(UUID tokenUuid);

    void deleteExpiredTokens();

    int countActiveTokens();

    List<Long> findOldestWaitingTokensIds(int limit);

    void updateOldestWaitingTokensToActive(List<Long> ids);

    int countWaitingTokensAhead(Long concertId, LocalDateTime referenceCreatedAt);

    void deleteByUuid(UUID tokenUuid);
}
