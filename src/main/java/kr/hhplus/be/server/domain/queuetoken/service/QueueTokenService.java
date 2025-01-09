package kr.hhplus.be.server.domain.queuetoken.service;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.queuetoken.dto.QueueTokenPositionResult;
import kr.hhplus.be.server.domain.queuetoken.dto.QueueTokenResult;
import kr.hhplus.be.server.domain.queuetoken.entity.QueueToken;
import java.util.List;
import java.util.UUID;

import kr.hhplus.be.server.domain.queuetoken.repository.QueueTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QueueTokenService {
    private final QueueTokenRepository queueTokenRepository;
    public static final int ACTIVE_TOKEN_MAX_COUNT = 30;
    public static final double PROCESSING_RATE_PER_SECONDS = 10;  // 1초당 처리 가능한 토큰 수

    @Transactional
    public QueueTokenResult issueWaitingToken(long userId, long concertId) {
        QueueToken queueToken = QueueToken.createWaitingToken(userId, concertId);
        QueueToken created = queueTokenRepository.save(queueToken);
        return QueueTokenResult.fromEntity(created);
    }

    @Transactional(readOnly = true)
    public QueueTokenPositionResult getWaitingTokenPositionAndRemainingTime(UUID tokenUuid) {
        QueueToken queueToken = getQueueToken(tokenUuid);

        if (queueToken.isActive()) {
            return QueueTokenPositionResult.fromEntity(queueToken, 0, 0);
        }

        int waitingPosition = calculateWaitingPosition(queueToken);
        int remainingSeconds = calculateRemainingSeconds(waitingPosition);

        return QueueTokenPositionResult.fromEntity(queueToken, waitingPosition, remainingSeconds);
    }

    private QueueToken getQueueToken(UUID tokenUuid) {
        return queueTokenRepository.findByTokenUuid(tokenUuid)
                .orElseThrow(() -> new EntityNotFoundException("Token is not found"));
    }

    private int calculateWaitingPosition(QueueToken queueToken) {
        int countAhead = queueTokenRepository.countWaitingTokensAhead(queueToken.getConcertId(), queueToken.getCreatedAt());
        return countAhead + 1;
    }

    private int calculateRemainingSeconds(int waitingPosition) {
        return (int) Math.ceil(waitingPosition / PROCESSING_RATE_PER_SECONDS);
    }

    @Transactional
    public void activateTokens() {
        queueTokenRepository.deleteExpiredTokens();

        int activeTokenCount = queueTokenRepository.countActiveTokens();
        if (activeTokenCount >= ACTIVE_TOKEN_MAX_COUNT) {
            return;
        }
        updateToActive(ACTIVE_TOKEN_MAX_COUNT - activeTokenCount);
    }

    private void updateToActive(int countToActivate) {
        List<Long> waitingTokens = queueTokenRepository.findOldestWaitingTokensIds(countToActivate);
        queueTokenRepository.updateOldestWaitingTokensToActive(waitingTokens);
    }

    @Transactional(readOnly = true)
    public boolean isTokenValid(UUID tokenUuid) {
        return getQueueToken(tokenUuid).isValid();
    }
}
