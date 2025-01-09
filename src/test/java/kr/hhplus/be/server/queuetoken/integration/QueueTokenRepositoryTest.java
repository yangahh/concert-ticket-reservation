package kr.hhplus.be.server.queuetoken.integration;

import kr.hhplus.be.server.domain.queuetoken.entity.QueueToken;
import kr.hhplus.be.server.domain.queuetoken.repository.QueueTokenRepository;
import kr.hhplus.be.server.infrastructure.queuetoken.repository.QueueTokenJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Testcontainers
class QueueTokenRepositoryTest {
    @Autowired
    private QueueTokenJpaRepository queueTokenJpaRepository;

    @Autowired
    private QueueTokenRepository queueTokenRepository;

    long concertId = 101L;

    @BeforeEach
    void setUp() {
        QueueToken token1 = QueueToken.createWaitingToken(1L, concertId);
        QueueToken token2 = QueueToken.createWaitingToken(2L, concertId);
        QueueToken token3 = QueueToken.createWaitingToken(3L, concertId);

        queueTokenJpaRepository.save(token1);
        queueTokenJpaRepository.save(token2);
        queueTokenJpaRepository.save(token3);
    }

    @AfterEach
    void tearDown() {
        queueTokenJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("QueueToken을 저장하면 DB에 정상적으로 저장된다.")
    void save_shouldPersistQueueToken() {
        // given
        QueueToken token = QueueToken.createWaitingToken(4L, concertId);

        // when
        QueueToken savedToken = queueTokenRepository.save(token);

        // then
        Optional<QueueToken> retrievedToken = queueTokenJpaRepository.findById(savedToken.getId());
        assertThat(retrievedToken).isPresent();
        assertThat(retrievedToken.get().getTokenUuid()).isEqualTo(token.getTokenUuid());
    }

    @Test
    @DisplayName("Token UUID로 QueueToken을 검색하면 해당 토큰이 반환된다.")
    void findByTokenUuid_shouldReturnMatchingToken() {
        // given
        QueueToken token = queueTokenJpaRepository.findAll().get(0);

        // when
        Optional<QueueToken> foundToken = queueTokenRepository.findByTokenUuid(token.getTokenUuid());

        // then
        assertThat(foundToken).isPresent();
        assertThat(foundToken.get().getId()).isEqualTo(token.getId());
    }

    @Test
    @DisplayName("expiredAt이 현재 시간보다 이전인 토큰을 삭제한다.")
    void deleteExpiredTokens_shouldRemoveExpiredTokens() {
        // given
        QueueToken token = QueueToken.builder()
                .tokenUuid(java.util.UUID.randomUUID())
                .userId(4L)
                .concertId(concertId)
                .isActive(false)
                .createdAt(LocalDateTime.now().minusMinutes(10))
                .expiredAt(LocalDateTime.now().minusMinutes(5))
                .build();
        queueTokenJpaRepository.save(token);

        // when
        queueTokenRepository.deleteExpiredTokens();

        // then
        List<QueueToken> tokens = queueTokenJpaRepository.findAll();
        assertThat(tokens).allMatch(t -> !t.getExpiredAt().isBefore(LocalDateTime.now()));
    }

    @Test
    @DisplayName("활성화된 토큰의 개수를 정확히 반환한다.")
    void countActiveTokens_shouldReturnCorrectCount() {
        // 검증1: 활성화된 토큰이 없을 때 결과값 확인
        int activeCount = queueTokenRepository.countActiveTokens();
        assertThat(activeCount).isEqualTo(0);

        // 검증2: 토큰 하나를 활상화 상태로 변경 후 결과값 확인
        QueueToken activeToken = queueTokenJpaRepository.findAll().get(0);
        activeToken.activate();
        queueTokenJpaRepository.save(activeToken);

        activeCount = queueTokenRepository.countActiveTokens();
        assertThat(activeCount).isEqualTo(1);
    }

    @Test
    @DisplayName("대기 중인 가장 오래된 토큰 ID를 제한된 개수만큼 반환한다.")
    void findOldestWaitingTokensIds_shouldReturnOldestTokens() {
        // 검증1: limit이 2일 때 가장 오래된 2개의 토큰 ID를 반환하는지 확인
        List<Long> ids = queueTokenRepository.findOldestWaitingTokensIds(2);
        assertThat(ids).hasSize(2);

        List<QueueToken> tokens =  queueTokenJpaRepository.findAll();

        // 검증2: 찾은 토큰들이 가장 오래된 토큰인지 확인
        QueueToken oldestToken = queueTokenJpaRepository.findById(ids.get(0)).orElseThrow();
        assertThat(oldestToken.getCreatedAt())
                .isEqualTo(tokens.get(0).getCreatedAt());

        QueueToken oldestToken2 = queueTokenJpaRepository.findById(ids.get(1)).orElseThrow();
        assertThat(oldestToken2.getCreatedAt())
                .isEqualTo(tokens.get(1).getCreatedAt());
    }

    @Test
    @DisplayName("가장 오래된 대기 토큰을 활성화 상태로 업데이트한다.")
    void updateOldestWaitingTokensToActive_shouldUpdateTokens() {
        List<Long> ids = queueTokenRepository.findOldestWaitingTokensIds(2);
        queueTokenRepository.updateOldestWaitingTokensToActive(ids);

        QueueToken updatedToken1 = queueTokenJpaRepository.findById(ids.get(0)).orElseThrow();
        assertThat(updatedToken1.isActive()).isTrue();
        QueueToken updatedToken2 = queueTokenJpaRepository.findById(ids.get(1)).orElseThrow();
        assertThat(updatedToken2.isActive()).isTrue();
        QueueToken updatedToken3 = queueTokenJpaRepository.findAll().get(2);
        assertThat(updatedToken3.isActive()).isFalse();
    }

    @Test
    @DisplayName("주어진 기준 시간 이전에 생성된 대기 상태인 토큰의 개수를 반환한다.")
    void countWaitingTokensAhead_shouldReturnCorrectCount() {
        // 검증1: 가장 먼저 만들어진 토큰의 앞에 있는 토큰이 없으므로 0 반환
        QueueToken firstToken = queueTokenJpaRepository.findAll().get(0);
        int result1 = queueTokenRepository.countWaitingTokensAhead(firstToken.getConcertId(), firstToken.getCreatedAt());

        assertThat(result1).isEqualTo(0);

        // 검증2: 가장 나중에 만들어진 토큰의 앞에 있는 토큰은 나머지 전부 이므로 2 반환
        QueueToken lastToken = queueTokenJpaRepository.findAll().get(2);
        int result2 = queueTokenRepository.countWaitingTokensAhead(lastToken.getConcertId(), lastToken.getCreatedAt());

        assertThat(result2).isEqualTo(2);
    }
}
