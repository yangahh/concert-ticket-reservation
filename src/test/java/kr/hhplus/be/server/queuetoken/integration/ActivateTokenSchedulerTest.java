package kr.hhplus.be.server.queuetoken.integration;

import kr.hhplus.be.server.application.queuetoken.scheduler.ActivateTokenScheduler;
import kr.hhplus.be.server.domain.queuetoken.entity.QueueToken;
import kr.hhplus.be.server.domain.queuetoken.entity.QueueTokenFactory;
import kr.hhplus.be.server.domain.queuetoken.service.QueueTokenService;
import kr.hhplus.be.server.infrastructure.queuetoken.repository.QueueTokenJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class ActivateTokenSchedulerTest {
    @Autowired
    private ActivateTokenScheduler scheduler;

    @Autowired
    private QueueTokenJpaRepository queueTokenJpaRepository;

    @BeforeEach
    void setUp() {
        List<QueueToken> tokens = new ArrayList<>();
        Clock mockClock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneId.systemDefault());

        // 유효한 토큰 30개(5개는 만료, 25개는 유효)
        for (int i = 0; i < 5; i++) {
            QueueToken token = QueueTokenFactory.createMock(UUID.randomUUID(), 1L, 1L, true, mockClock);
            tokens.add(token);
        }
        for (int i = 0; i < QueueTokenService.ACTIVE_TOKEN_MAX_COUNT - 5; i++) {
            QueueToken token = QueueTokenFactory.createMock(UUID.randomUUID(), 1L, 1L, true);
            tokens.add(token);
        }
        // waiting 상태의 토큰 10개
        for (int i = 0; i < 10; i++) {
            QueueToken token = QueueTokenFactory.createMock(UUID.randomUUID(), 1L, 1L, false);
            tokens.add(token);
        }
        queueTokenJpaRepository.saveAllAndFlush(tokens);
    }

    @AfterEach
    void tearDown() {
        queueTokenJpaRepository.deleteAll();
    }

    @DisplayName("대기열에서 active 할 수 있는 토큰의 수 만큼 wait 상태의 토큰을 active 상태로 변경한다.")
    @Test
    void activeTokenSchedulerSuccessTest() {
        // when
        scheduler.execute();

        // then
        assertThat(queueTokenJpaRepository.countByIsActive(true)).isEqualTo(QueueTokenService.ACTIVE_TOKEN_MAX_COUNT);
        assertThat(queueTokenJpaRepository.countByIsActive(false)).isEqualTo(5);

    }
}
