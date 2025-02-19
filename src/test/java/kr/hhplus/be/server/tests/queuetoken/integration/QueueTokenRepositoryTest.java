package kr.hhplus.be.server.tests.queuetoken.integration;

import kr.hhplus.be.server.domain.queuetoken.entity.QueueToken;
import kr.hhplus.be.server.domain.queuetoken.entity.QueueTokenFactory;
import kr.hhplus.be.server.domain.queuetoken.repository.QueueTokenRepository;
import kr.hhplus.be.server.infrastructure.repository.queuetoken.ActiveTokenRedisTemplate;
import kr.hhplus.be.server.infrastructure.repository.queuetoken.WaitingTokenRedisTemplate;
import kr.hhplus.be.server.utils.time.TimeProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class QueueTokenRepositoryTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private QueueTokenRepository queueTokenRepository;

    @Autowired
    private TimeProvider timeProvider;

    @Autowired
    private WaitingTokenRedisTemplate waitingTokenRedisTemplate;

    @Autowired
    private ActiveTokenRedisTemplate activeTokenRedisTemplate;

    long concertId = 101L;

    QueueToken token1;
    QueueToken token2;
    QueueToken token3;

    @BeforeEach
    void setUp() throws InterruptedException {
        token1 = QueueToken.createWaitingToken(1L, concertId, timeProvider);
        Thread.sleep(1); // 1ms 지연
        token2 = QueueToken.createWaitingToken(2L, concertId, timeProvider);
        Thread.sleep(1); // 1ms 지연
        token3 = QueueToken.createWaitingToken(3L, concertId, timeProvider);

        queueTokenRepository.save(token1);
        queueTokenRepository.save(token2);
        queueTokenRepository.save(token3);
    }

    @AfterEach
    void tearDown() {
        redisTemplate.delete(WaitingTokenRedisTemplate.getKey(concertId));
        redisTemplate.delete(ActiveTokenRedisTemplate.getKey(concertId));
    }

    @Test
    @DisplayName("QueueToken을 저장하면 DB에 정상적으로 저장된다.")
    void save_shouldPersistQueueToken() {
        // given
        QueueToken token = QueueToken.createWaitingToken(4L, concertId, timeProvider);

        // when
        QueueToken savedToken = queueTokenRepository.save(token);

        // then
        Double score = waitingTokenRedisTemplate.getScore(concertId, savedToken.getTokenUuid());
        assertThat(score).isNotNull();
    }

    @Test
    @DisplayName("Token UUID로 QueueToken을 검색하면 해당 토큰이 반환된다.")
    void findByConcertIdAndTokenUuid_shouldReturnMatchingToken() {
        // given
        Set<Object> token = waitingTokenRedisTemplate.range(concertId, 0, 0);
        UUID tokenUuid = UUID.fromString(token.iterator().next().toString());

        // when
        Optional<QueueToken> foundToken = queueTokenRepository.findByConcertIdAndTokenUuid(concertId, tokenUuid);

        // then
        assertThat(foundToken).isPresent();
        assertThat(foundToken.get().getTokenUuid()).isEqualTo(tokenUuid);
    }

    @DisplayName("findByConcertIdAndTokenUuid로 waitingToken과 activeToken을 검색하면 각 상태에 맞는 토큰이 반환된다.")
    @Test
    void findByConcertIdAndTokenUuid_shouldReturnWaitingTokenAndActiveToken() {
        // given
        QueueToken waitingToken = QueueToken.createWaitingToken(1L, concertId, timeProvider);
        queueTokenRepository.save(waitingToken);
        QueueToken activeToken = QueueToken.createWaitingToken(2L, concertId, timeProvider);
        activeToken.activate();
        queueTokenRepository.save(activeToken);

        // when
        Optional<QueueToken> foundWaitingToken = queueTokenRepository.findByConcertIdAndTokenUuid(concertId, waitingToken.getTokenUuid());
        Optional<QueueToken> foundActiveToken = queueTokenRepository.findByConcertIdAndTokenUuid(concertId, activeToken.getTokenUuid());

        // then
        assertThat(foundWaitingToken).isPresent();
        assertThat(foundWaitingToken.get().isActive()).isFalse();
        assertThat(foundActiveToken).isPresent();
        assertThat(foundActiveToken.get().isActive()).isTrue();
    }

    @DisplayName("존재하지 않는 uuid로 findByConcertIdAndTokenUuid를 호출하면 빈 Optional을 반환한다.")
    @Test
    void findByConcertIdAndTokenUuid_shouldReturnEmptyOptionalWithNotExistingUuid() {
        // given
        UUID notExistingUuid = UUID.randomUUID();

        // when
        Optional<QueueToken> foundToken = queueTokenRepository.findByConcertIdAndTokenUuid(concertId, notExistingUuid);

        // then
        assertThat(foundToken).isEmpty();
    }

    @Test
    @DisplayName("expiredAt이 현재 시간보다 이전인 토큰을 삭제한다.")
    void deleteExpiredTokens_shouldRemoveExpiredTokens() {
        // given
        Clock mockClock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneId.systemDefault());
        QueueToken expiredToken = QueueTokenFactory.createMock(UUID.randomUUID(), 4L, concertId, false, mockClock);
        queueTokenRepository.save(expiredToken);

        // when
        queueTokenRepository.deleteExpiredTokens(concertId);

        // then
        Set<TypedTuple<Object>> tokens = waitingTokenRedisTemplate.rangeWithScores(concertId, 0, -1);
        assertThat(tokens).hasSize(3);
        assertThat(tokens).allMatch(t -> t.getScore() > Instant.now().toEpochMilli());  // 만료되지 않은 토큰만 남아있어야 함
    }

    @Test
    @DisplayName("활성화된 토큰의 개수를 정확히 반환한다.")
    void countActiveTokens_shouldReturnCorrectCount() {
        // 검증1: 활성화된 토큰이 없을 때 결과값 확인
        int activeCount = queueTokenRepository.countActiveTokens(concertId);
        assertThat(activeCount).isEqualTo(0);

        // 검증2: 활상화 상태의 토큰 1개를 저장 후 결과값 확인
        QueueToken activeToken = QueueToken.createWaitingToken(1L, concertId, timeProvider);
        activeToken.activate();
        queueTokenRepository.save(activeToken);

        activeCount = queueTokenRepository.countActiveTokens(concertId);
        assertThat(activeCount).isEqualTo(1);
    }

    @DisplayName("""
        activateTokensByConcertId를 호출하여 대기열에서 가장 오래된 2개의 토큰을 활성 토큰으로 전환한다.
        이후, waiting 큐에 1개의 토큰이 남아있어야 하고, active 큐에 2개의 토큰이 있어야 한다.
        """)
    @Test
    void activateTokensByConcertIdTest(){
        // when
        queueTokenRepository.activateTokensByConcertId(concertId, 2);

        // then
        assertThat(waitingTokenRedisTemplate.size(concertId)).isEqualTo(1);
        assertThat(activeTokenRedisTemplate.size(concertId)).isEqualTo(2);
    }

    @Test
    @DisplayName("주어진 기준 시간 이전에 생성된 대기 상태인 토큰의 개수를 반환한다.")
    void countWaitingTokensAhead_shouldReturnCorrectCount(){
        // 검증1: 가장 먼저 만들어진 토큰의 앞에 있는 토큰이 없으므로 0 반환
        Optional<QueueToken> firstToken = queueTokenRepository.findByConcertIdAndTokenUuid(token1.getConcertId(), token1.getTokenUuid());
        int result1 = queueTokenRepository.countWaitingTokensAhead(firstToken.get());

        assertThat(result1).isEqualTo(0);

        // 검증2: 가장 나중에 만들어진 토큰의 앞에 있는 토큰은 나머지 전부 이므로 2 반환
        Optional<QueueToken> lastToken = queueTokenRepository.findByConcertIdAndTokenUuid(token3.getConcertId(), token3.getTokenUuid());
        int result2 = queueTokenRepository.countWaitingTokensAhead(lastToken.get());

        assertThat(result2).isEqualTo(2);
    }
}
