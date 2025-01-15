package kr.hhplus.be.server.infrastructure.queuetoken.repository;

import kr.hhplus.be.server.domain.queuetoken.entity.QueueToken;
import kr.hhplus.be.server.domain.queuetoken.repository.QueueTokenRepository;
import kr.hhplus.be.server.utils.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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
    public Optional<QueueToken> findByTokenUuid(UUID tokenUuid) {
        return jpaRepository.findByTokenUuid(tokenUuid);
    }

    @Override
    public void deleteExpiredTokens() {
        jpaRepository.deleteByExpiredAtBefore(timeProvider.now());
    }

    @Override
    public int countActiveTokens() {
        return jpaRepository.countByIsActive(true);
    }

    @Override
    public List<Long> findOldestWaitingTokensIds(int limit) {
        return jpaRepository.findOldestWaitingTokenIds(Pageable.ofSize(limit));
    }

    @Override
    public void updateOldestWaitingTokensToActive(List<Long> ids) {
        jpaRepository.updateOldestWaitingTokensToActive(ids);
    }

    @Override
    public int countWaitingTokensAhead(Long concertId, LocalDateTime referenceCreatedAt) {
        return jpaRepository.countByConcertIdAndCreatedAtBeforeAndExpiredAtAfterAndIsActive(
            concertId, referenceCreatedAt, timeProvider.now(), false);
    }

    @Override
    public void deleteByUuid(UUID tokenUuid) {
        jpaRepository.deleteByTokenUuid(tokenUuid);
    }
}
