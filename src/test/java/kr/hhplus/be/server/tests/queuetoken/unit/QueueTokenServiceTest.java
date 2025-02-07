package kr.hhplus.be.server.tests.queuetoken.unit;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.queuetoken.dto.QueueTokenPositionResult;
import kr.hhplus.be.server.domain.queuetoken.dto.QueueTokenResult;
import kr.hhplus.be.server.domain.queuetoken.entity.QueueToken;
import kr.hhplus.be.server.domain.queuetoken.repository.QueueTokenRepository;
import kr.hhplus.be.server.domain.queuetoken.service.QueueTokenService;
import kr.hhplus.be.server.utils.time.TimeProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class QueueTokenServiceTest {
    @InjectMocks
    private QueueTokenService sut;

    @Mock
    private QueueTokenRepository queueTokenRepository;

    @Mock
    private TimeProvider timeProvider;

    long concertId = 100L;

    @DisplayName("토큰 발급: 사용자 ID와 콘서트 ID를 입력받아 비활성상태의 토큰을 정상적으로 발급한다.")
    @Test
    void issueWaitingTokenSuccessTest() {
        // given
        long userId = 1L;
        given(timeProvider.now()).willReturn(LocalDateTime.now());
        QueueToken newToken = QueueToken.createWaitingToken(userId, concertId, timeProvider);
        given(queueTokenRepository.save(any(QueueToken.class))).willReturn(newToken);

        // when
        QueueTokenResult saved = sut.issueWaitingToken(userId, concertId);

        // then
        assertThat(saved.concertId()).isEqualTo(newToken.getConcertId());
        assertThat(saved.tokenUuid()).isEqualTo(newToken.getTokenUuid());
        assertThat(saved.isActive()).isFalse();
        assertThat(saved.createdAt()).isEqualTo(newToken.getCreatedAt());
        assertThat(saved.expiredAt()).isEqualTo(newToken.getExpiredAt());
    }

    @DisplayName("대기열 순번 조회: 존재하지 않는 토큰 UUID로 조회하면 예외가 발생한다.")
    @Test
    void shouldNotFoundExceptionWhenGetWaitingTokenPositionWithNonExistingTokenUuid() {
        // given
        UUID nonExistingTokenUuid = UUID.randomUUID();
        given(queueTokenRepository.findByConcertIdAndTokenUuid(concertId, nonExistingTokenUuid)).willReturn(Optional.empty());

        // when  // then
        assertThatThrownBy(() -> sut.getWaitingTokenPositionAndRemainingTime(concertId, nonExistingTokenUuid))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Token is not found");
    }

    @DisplayName("대기열 순번 조회: 이미 Active상태인 토큰으로 대기열 순번을 조회하면 대기 순번 0과 남은 시간 0을 반환한다.")
    @Test
    void getWaitingTokenPositionWithActiveTokenTest() {
        // given
        given(timeProvider.now()).willReturn(LocalDateTime.now());

        QueueToken queueToken = QueueToken.createWaitingToken(1L, concertId, timeProvider);
        UUID tokenUuid = queueToken.getTokenUuid();
        queueToken.activate();
        given(queueTokenRepository.findByConcertIdAndTokenUuid(concertId, tokenUuid)).willReturn(Optional.of(queueToken));

        // when
        QueueTokenPositionResult result = sut.getWaitingTokenPositionAndRemainingTime(concertId, tokenUuid);

        // then
        assertThat(result.position()).isEqualTo(0);
        assertThat(result.remainingSeconds()).isEqualTo(0);
    }

    @DisplayName("대기열 순번 조회: 내 앞에 99명이 있을 때, 대기 순번 100번, 예상 남은 시간 10초를 반환한다.")
    @Test
    void getWaitingTokenPositionAndRemainingTimeSuccessTest() {
        // given
        given(timeProvider.now()).willReturn(LocalDateTime.now());

        UUID tokenUuid = UUID.randomUUID();
        QueueToken queueToken = QueueToken.createWaitingToken(1L, concertId, timeProvider);
        given(queueTokenRepository.findByConcertIdAndTokenUuid(concertId, tokenUuid)).willReturn(Optional.of(queueToken));
        given(queueTokenRepository.countWaitingTokensAhead(queueToken)).willReturn(99);

        // when
        QueueTokenPositionResult result = sut.getWaitingTokenPositionAndRemainingTime(concertId, tokenUuid);

        // then
        assertThat(result.position()).isEqualTo(100);
        assertThat(result.remainingSeconds()).isEqualTo(10);
    }

    @DisplayName("토큰 활성화: 만료된 토큰을 삭제하고, 대기열에 active 토큰이 최대 개수만큼 있다면 아무 작업 없이 종료한다.")
    @Test
    void activateTokensWhenActiveTokenCountIsMaxTest() {
        // given
        given(queueTokenRepository.countActiveTokens(concertId)).willReturn(QueueTokenService.ACTIVE_TOKEN_MAX_COUNT);

        // when
        sut.activateTokens(concertId);

        // then
        then(queueTokenRepository).should().deleteExpiredTokens(concertId);
        then(queueTokenRepository).should(never()).activateTokensByConcertId(anyLong(), anyInt());
    }

    @DisplayName("토큰 활성화: 대기열에 active 토큰이 최대 개수보다 적을 때, 최대 개수에서 모자란 수만큼 대기열에서 가장 오래된 waiting 토큰을 활성화한다.")
    @Test
    void activateTokensWhenActiveTokenCountIsLessThanMaxTest() {
        // given
        given(queueTokenRepository.countActiveTokens(concertId)).willReturn(QueueTokenService.ACTIVE_TOKEN_MAX_COUNT - 5);
        // given(queueTokenRepository.findOldestWaitingTokensIds(anyInt())).willReturn(List.of(1L, 2L, 3L, 4L, 5L));

        // when
        sut.activateTokens(concertId);

        // then
        then(queueTokenRepository).should().activateTokensByConcertId(concertId, 5);
    }

    @DisplayName("토큰 유효성 검증: 존재하지 않는 토큰 UUID로 검증을 하면 예외가 발생한다")
    @Test
    void shouldNotFoundExceptionWhenValidateTokenWithNonExistingTokenUuid() {
        // given
        UUID nonExistingTokenUuid = UUID.randomUUID();
        given(queueTokenRepository.findByConcertIdAndTokenUuid(concertId, nonExistingTokenUuid)).willReturn(Optional.empty());

        // when // then
        assertThatThrownBy(() -> sut.isTokenValid(concertId, nonExistingTokenUuid))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Token is not found");
    }

    @DisplayName("토큰 유효성 검증: waiting 토큰이면 유효하지 않은 토큰이다.")
    @Test
    void shoudReturnFalseWhenValidateWithWaitingToken() {
        // given
        given(timeProvider.now()).willReturn(LocalDateTime.now());

        UUID tokenUuid = UUID.randomUUID();
        QueueToken waitingToken = QueueToken.createWaitingToken(1L, concertId, timeProvider);
        given(queueTokenRepository.findByConcertIdAndTokenUuid(concertId, tokenUuid)).willReturn(Optional.of(waitingToken));

        // when
        boolean result = sut.isTokenValid(concertId, tokenUuid);

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("토큰 유효성 검증: active 토큰이지만 expiredAt이 현재 시간보다 이전이면 유효하지 않다.")
    @Test
    void validateTokenSuccessTest() {
        // given
        given(timeProvider.now()).willReturn(LocalDateTime.now());

        QueueToken queueToken = QueueToken.createWaitingToken(1L, concertId, timeProvider);
        UUID tokenUuid = queueToken.getTokenUuid();
        queueToken.activate();
        given(queueTokenRepository.findByConcertIdAndTokenUuid(concertId, tokenUuid)).willReturn(Optional.of(queueToken));

        // when
        boolean isValid = sut.isTokenValid(concertId, tokenUuid);

        // then
        assertThat(isValid).isTrue();
    }


}
