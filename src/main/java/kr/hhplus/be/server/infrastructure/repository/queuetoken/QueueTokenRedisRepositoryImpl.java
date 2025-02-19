package kr.hhplus.be.server.infrastructure.repository.queuetoken;

import kr.hhplus.be.server.domain.queuetoken.entity.QueueToken;
import kr.hhplus.be.server.domain.queuetoken.repository.QueueTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
@Primary
@RequiredArgsConstructor
public class QueueTokenRedisRepositoryImpl implements QueueTokenRepository {
    private final WaitingTokenRedisTemplate waitingTokenRedisTemplate;
    private final ActiveTokenRedisTemplate activeTokenRedisTemplate;

    @Override
    public QueueToken save(QueueToken queueToken) {
        double expiredTimeStamp = (double) queueToken.getExpiredAt().atZone(ZoneOffset.UTC).toInstant().toEpochMilli();

        if (queueToken.isActive()) {
            activeTokenRedisTemplate.add(queueToken.getConcertId(), queueToken.getTokenUuid(), expiredTimeStamp);
        } else {
            waitingTokenRedisTemplate.add(queueToken.getConcertId(), queueToken.getTokenUuid(), expiredTimeStamp);
        }
        return queueToken;
    }

    @Override
    public Optional<QueueToken> findByConcertIdAndTokenUuid(Long concertId, UUID tokenUuid) {
        Double waitingScore = waitingTokenRedisTemplate.getScore(concertId, tokenUuid);
        if (waitingScore != null) {
            return Optional.of(QueueToken.fromRedis(concertId, tokenUuid, false));
        }

        Double activeScore = activeTokenRedisTemplate.getScore(concertId, tokenUuid);
        if (activeScore != null) {
            return Optional.of(QueueToken.fromRedis(concertId, tokenUuid, true));
        }
        return Optional.empty();
    }

    @Override
    public int countWaitingTokensAhead(QueueToken queueToken) {
        return waitingTokenRedisTemplate.getRank(queueToken.getConcertId(), queueToken.getTokenUuid()).intValue();
    }

    @Override
    public int countActiveTokens(Long concertId) {
        return activeTokenRedisTemplate.size(concertId).intValue();
    }

    @Override
    public void activateTokensByConcertId(Long concertId, int countToActivate) {
        Set<TypedTuple<Object>> result = waitingTokenRedisTemplate.popMin(concertId, countToActivate);

        for (TypedTuple<Object> tuple : result) {
            double expiredTimeStamp = (double) Instant.now().plus(Duration.ofMinutes(10)).toEpochMilli();
            UUID tokenUuid = UUID.fromString(tuple.getValue().toString());
            activeTokenRedisTemplate.add(concertId, tokenUuid, expiredTimeStamp);
        }
    }

    @Override
    public void deleteByConcertIdAndUuid(Long concertId, UUID tokenUuid) {
        activeTokenRedisTemplate.remove(concertId, tokenUuid);
    }

    @Override
    public void deleteExpiredTokens(Long concertId) {
        long currentTime = Instant.now().toEpochMilli();

        waitingTokenRedisTemplate.removeRangeByScore(concertId, 0, currentTime);
        activeTokenRedisTemplate.removeRangeByScore(concertId, 0, currentTime);
    }
}
