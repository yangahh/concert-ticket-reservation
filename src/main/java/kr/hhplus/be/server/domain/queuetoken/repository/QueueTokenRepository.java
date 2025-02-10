package kr.hhplus.be.server.domain.queuetoken.repository;

import kr.hhplus.be.server.domain.queuetoken.entity.QueueToken;

import java.util.Optional;
import java.util.UUID;

public interface QueueTokenRepository {
    QueueToken save(QueueToken queueToken);

    Optional<QueueToken> findByConcertIdAndTokenUuid(Long concertId, UUID tokenUuid);

    int countActiveTokens(Long concertId);

    void deleteExpiredTokens(Long concertId);

    void activateTokensByConcertId(Long concertId, int countToActivate);

    int countWaitingTokensAhead(QueueToken queueToken);

    void deleteByConcertIdAndUuid(Long concertId, UUID tokenUuid);
}
