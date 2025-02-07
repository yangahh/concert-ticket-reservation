package kr.hhplus.be.server.tests.queuetoken.integration;

import kr.hhplus.be.server.application.queuetoken.scheduler.ActivateTokenScheduler;
import kr.hhplus.be.server.domain.concert.entity.Concert;
import kr.hhplus.be.server.domain.concert.service.ConcertService;
import kr.hhplus.be.server.domain.queuetoken.entity.QueueToken;
import kr.hhplus.be.server.domain.queuetoken.entity.QueueTokenFactory;
import kr.hhplus.be.server.domain.queuetoken.repository.QueueTokenRepository;
import kr.hhplus.be.server.domain.queuetoken.service.QueueTokenService;
import kr.hhplus.be.server.infrastructure.concert.repository.ConcertJpaRepository;
import kr.hhplus.be.server.infrastructure.queuetoken.repository.ActiveTokenRedisTemplate;
import kr.hhplus.be.server.infrastructure.queuetoken.repository.WaitingTokenRedisTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ActivateTokenSchedulerTest {
    @Autowired
    private ActivateTokenScheduler scheduler;

    @Autowired
    private QueueTokenRepository queueTokenRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private WaitingTokenRedisTemplate waitingTokenRedisTemplate;

    @Autowired
    private ActiveTokenRedisTemplate activeTokenRedisTemplate;

    @Autowired
    private ConcertJpaRepository concertJpaRepository;

    Long concertId;

    int activeTokenCnt = 25;
    int expiredActiveTokenCnt = 5;
    int waitingTokenCnt = 10;

    @BeforeEach
    void setUp() {
        Concert concert = concertJpaRepository.save(Concert.create("concert1", LocalDateTime.now()));
        concertId = concert.getId();

        Clock mockClock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneId.systemDefault());

        // active 토큰 30개(5개는 만료, 25개는 유효)
        for (int i = 0; i < expiredActiveTokenCnt; i++) {
            QueueToken expired = QueueTokenFactory.createMock(UUID.randomUUID(), 1L, concertId, true, mockClock);
            queueTokenRepository.save(expired);
        }
        for (int i = 0; i < activeTokenCnt; i++) {
            QueueToken valid = QueueTokenFactory.createMock(UUID.randomUUID(), 1L, concertId, true);
            queueTokenRepository.save(valid);
        }
        // waiting 상태의 토큰 10개
        for (int i = 0; i < waitingTokenCnt; i++) {
            QueueToken token = QueueTokenFactory.createMock(UUID.randomUUID(), 1L, concertId, false);
            queueTokenRepository.save(token);
        }
    }

    @AfterEach
    void tearDown() {
        concertJpaRepository.deleteAll();
        redisTemplate.delete(WaitingTokenRedisTemplate.getKey(concertId));
        redisTemplate.delete(ActiveTokenRedisTemplate.getKey(concertId));
    }

    @DisplayName("대기열에서 active 할 수 있는 토큰의 수 만큼 waiting 상태의 토큰을 active 상태로 변경한다.")
    @Test
    void activeTokenSchedulerSuccessTest() {
        // when
        scheduler.execute();

        // then
        assertThat(activeTokenRedisTemplate.size(concertId)).isEqualTo(activeTokenCnt + waitingTokenCnt);
        assertThat(waitingTokenRedisTemplate.size(concertId)).isEqualTo(0);

    }
}
