package kr.hhplus.be.server.tests.queuetoken.unit;

import kr.hhplus.be.server.domain.queuetoken.entity.QueueToken;
import kr.hhplus.be.server.utils.time.TimeProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class QueueTokenEntityTest {
    private static final Long USER_ID = 1L;
    private static final Long CONCERT_ID = 100L;

    @Mock
    private static TimeProvider timeProvider;

    @DisplayName("createWaitingToken 메소드를 사용해서 uuid, createdAt, expiredAt이 포함된 waiting 상태의 토큰을 생성할 수 있다.")
    @Test
    void createWaitingTokenTokenTest() {
        // given
        given(timeProvider.now()).willReturn(LocalDateTime.now());

        // when
        QueueToken token = QueueToken.createWaitingToken(USER_ID, CONCERT_ID, timeProvider);

        // then
        assertThat(token.getTokenUuid()).isInstanceOf(UUID.class);
        assertThat(token.isActive()).isFalse();
        assertThat(token.getCreatedAt()).isCloseTo(LocalDateTime.now(), within(1, SECONDS));
        assertThat(token.getExpiredAt()).isEqualTo(token.getCreatedAt().plusMinutes(10));
    }

    @DisplayName("activate 메소드를 사용해서 토큰을 활성화 할 수 있다.")
    @Test
    void activateTest() {
        // given
        given(timeProvider.now()).willReturn(LocalDateTime.now());
        QueueToken token = QueueToken.createWaitingToken(USER_ID, CONCERT_ID, timeProvider);

        // when
        token.activate();

        // then
        assertThat(token.isActive()).isTrue();
    }

    @DisplayName("isExpired 메소드를 사용해서 토큰이 만료되었는지 확인할 수 있다.")
    @Test
    void isExpiredTest() {
        // given
        LocalDateTime mockPastTime = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        given(timeProvider.now()).willReturn(mockPastTime);
        QueueToken expiredToken = QueueToken.createWaitingToken(USER_ID, CONCERT_ID, timeProvider);

        given(timeProvider.now()).willReturn(LocalDateTime.now());
        QueueToken notExpiredToken = QueueToken.createWaitingToken(USER_ID, CONCERT_ID, timeProvider);

        // when
        boolean expiredTokenIsExpired = expiredToken.isExpired(timeProvider);
        boolean notExpiredTokenIsExpired = notExpiredToken.isExpired(timeProvider);

        // then
        assertThat(expiredTokenIsExpired).isTrue();
        assertThat(notExpiredTokenIsExpired).isFalse();
    }


    @DisplayName("토큰이  활성화 상태이고 만료되지 않았을 때 isValid 메소드를 사용해서 토큰이 유효한지 확인할 수 있다.")
    @Test
    void isValidTest() {
        // given
        LocalDateTime mockPastTime = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        given(timeProvider.now()).willReturn(mockPastTime);
        QueueToken waitingAndExpiredToken = QueueToken.createWaitingToken(USER_ID, CONCERT_ID, timeProvider);
        QueueToken activeAndExpiredToken = QueueToken.createWaitingToken(USER_ID, CONCERT_ID, timeProvider);
        activeAndExpiredToken.activate();

        given(timeProvider.now()).willReturn(LocalDateTime.now());
        QueueToken waitingAndNotExpiredToken = QueueToken.createWaitingToken(USER_ID, CONCERT_ID, timeProvider);
        QueueToken activeAndNotExpiredToken = QueueToken.createWaitingToken(USER_ID, CONCERT_ID, timeProvider);
        activeAndNotExpiredToken.activate();

        // when
        boolean waitingAndExpiredTokenIsValid = waitingAndExpiredToken.isValid(timeProvider);
        boolean waitingAndNotExpiredTokenIsValid = waitingAndNotExpiredToken.isValid(timeProvider);
        boolean activeAndExpiredTokenIsValid = activeAndExpiredToken.isValid(timeProvider);
        boolean activeAndNotExpiredTokenIsValid = activeAndNotExpiredToken.isValid(timeProvider);

        // then
        assertThat(waitingAndExpiredTokenIsValid).isFalse();
        assertThat(waitingAndNotExpiredTokenIsValid).isFalse();
        assertThat(activeAndExpiredTokenIsValid).isFalse();
        assertThat(activeAndNotExpiredTokenIsValid).isTrue();
    }
}
