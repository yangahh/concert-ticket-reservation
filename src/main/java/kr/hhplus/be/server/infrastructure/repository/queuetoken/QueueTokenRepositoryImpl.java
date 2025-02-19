package kr.hhplus.be.server.infrastructure.repository.queuetoken;

import kr.hhplus.be.server.domain.queuetoken.entity.QueueToken;
import kr.hhplus.be.server.domain.queuetoken.repository.QueueTokenRepository;
import kr.hhplus.be.server.utils.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class QueueTokenRepositoryImpl implements QueueTokenRepository {
    private final QueueTokenJpaRepository jpaRepository;
    private final TimeProvider timeProvider;

    @Override
    public QueueToken save(QueueToken queueToken) {
        return jpaRepository.save(queueToken);
    }

    @Override
    public Optional<QueueToken> findByConcertIdAndTokenUuid(Long concertId, UUID tokenUuid) {
        return jpaRepository.findByTokenUuid(tokenUuid);
    }

    @Override
    public void deleteExpiredTokens(Long concertId) {
        jpaRepository.deleteByExpiredAtBefore(timeProvider.now());
    }

    @Override
    public int countActiveTokens(Long concertId) {
        return jpaRepository.countByConcertIdAndIsActive(concertId, true);
    }

    @Override
    public void activateTokensByConcertId(Long concertId, int countToActivate) {
        List<Long> oldestWaitingTokenIds = jpaRepository.findOldestWaitingTokenIds(Pageable.ofSize(countToActivate));
        jpaRepository.updateOldestWaitingTokensToActive(oldestWaitingTokenIds);
    }

    @Override
    public int countWaitingTokensAhead(QueueToken queueToken) {
        return jpaRepository.countByConcertIdAndCreatedAtBeforeAndExpiredAtAfterAndIsActive(
            queueToken.getConcertId(), queueToken.getCreatedAt(), timeProvider.now(), false);
    }

    @Override
    public void deleteByConcertIdAndUuid(Long concertId, UUID tokenUuid) {
        jpaRepository.deleteByTokenUuid(tokenUuid);
    }
}
