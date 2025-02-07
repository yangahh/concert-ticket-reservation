package kr.hhplus.be.server.domain.queuetoken.service;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.queuetoken.dto.QueueTokenPositionResult;
import kr.hhplus.be.server.domain.queuetoken.dto.QueueTokenResult;
import kr.hhplus.be.server.domain.queuetoken.entity.QueueToken;
import kr.hhplus.be.server.domain.queuetoken.exception.InvalidToken;
import kr.hhplus.be.server.domain.queuetoken.repository.QueueTokenRepository;
import kr.hhplus.be.server.utils.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QueueTokenService {
    private final QueueTokenRepository queueTokenRepository;
    private final TimeProvider timeProvider;
    public static final int ACTIVE_TOKEN_MAX_COUNT = 500;  // 최대 active 토큰 수
    public static final int CONVERT_RATE_PER_10_SECONDS = 30;  // 10초당 active로 전환되는 토큰 수
    public static final double PROCESSING_RATE_PER_SECONDS = 3;  // 1초당 처리 가능한 토큰 수

    @Transactional
    public QueueTokenResult issueWaitingToken(long userId, long concertId) {
        QueueToken queueToken = QueueToken.createWaitingToken(userId, concertId, timeProvider);
        QueueToken created = queueTokenRepository.save(queueToken);
        return QueueTokenResult.fromEntity(created);
    }

    @Transactional(readOnly = true)
    public QueueTokenPositionResult getWaitingTokenPositionAndRemainingTime(Long concertId, UUID tokenUuid) {
        QueueToken queueToken = getQueueToken(concertId, tokenUuid);

        if (queueToken.isValid(timeProvider)) {
            return QueueTokenPositionResult.fromEntity(queueToken, 0, 0);
        }

        if (queueToken.isExpired(timeProvider)) {
            throw new InvalidToken("Token is expired");
        }

        int waitingPosition = calculateWaitingPosition(queueToken);
        int remainingSeconds = calculateRemainingSeconds(waitingPosition);

        return QueueTokenPositionResult.fromEntity(queueToken, waitingPosition, remainingSeconds);
    }

    private QueueToken getQueueToken(Long concertId, UUID tokenUuid) {
        return queueTokenRepository.findByConcertIdAndTokenUuid(concertId, tokenUuid)
                .orElseThrow(() -> new EntityNotFoundException("Token is not found (id = " + tokenUuid + ")"));
    }

    private int calculateWaitingPosition(QueueToken queueToken) {
        int countAhead = queueTokenRepository.countWaitingTokensAhead(queueToken);
        return countAhead + 1;
    }

    private int calculateRemainingSeconds(int waitingPosition) {
        return (int) Math.ceil(waitingPosition / PROCESSING_RATE_PER_SECONDS);
    }

    @Transactional
    public void activateTokens(Long concertId) {
        queueTokenRepository.deleteExpiredTokens(concertId);

        int activeTokenCount = queueTokenRepository.countActiveTokens(concertId);
        if (activeTokenCount >= ACTIVE_TOKEN_MAX_COUNT) {
            return;
        }
        queueTokenRepository.activateTokensByConcertId(concertId, CONVERT_RATE_PER_10_SECONDS);
    }

    @Transactional(readOnly = true)
    public boolean isTokenValid(Long concertId, UUID tokenUuid) {
        return getQueueToken(concertId, tokenUuid).isValid(timeProvider);
    }

    @Transactional
    public void deleteToken(Long concertId, UUID tokenUuid) {
        QueueToken queueToken = getQueueToken(concertId, tokenUuid);
        queueTokenRepository.deleteByConcertIdAndUuid(concertId, queueToken.getTokenUuid());
    }
}
