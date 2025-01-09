package kr.hhplus.be.server.queuetoken.unit;

import kr.hhplus.be.server.domain.queuetoken.entity.QueueToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class QueueTokenEntityTest {
    private static final Long USER_ID = 1L;
    private static final Long CONCERT_ID = 100L;

    @DisplayName("createWaitingToken 메소드를 사용해서 uuid, createdAt, expiredAt이 포함된 waiting 상태의 토큰을 생성할 수 있다.")
    @Test
    void createWaitingTokenTokenTest() {
        // when
        QueueToken token = QueueToken.createWaitingToken(USER_ID, CONCERT_ID);

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
        QueueToken token = QueueToken.createWaitingToken(USER_ID, CONCERT_ID);

        // when
        token.activate();

        // then
        assertThat(token.isActive()).isTrue();
    }

    @DisplayName("isValid 메소드를 사용해서 토큰이 유효한지 확인할 수 있다. 초기 생성된 토큰은 isActive가 False이기 때문에 유효하지 않다.")
    @Test
    void isValidtest() {
        // given
        QueueToken waitingToken = QueueToken.createWaitingToken(USER_ID, CONCERT_ID);
        QueueToken activeToken = QueueToken.createWaitingToken(USER_ID, CONCERT_ID);
        activeToken.activate();

        // when
        boolean waitingTokenIsValid = waitingToken.isValid();
        boolean activeTokenIsValid = activeToken.isValid();

        // then
        assertThat(waitingToken.isActive()).isFalse();
        assertThat(waitingToken.getExpiredAt()).isAfter(LocalDateTime.now());
        assertThat(waitingTokenIsValid).isFalse();

        assertThat(activeToken.isActive()).isTrue();
        assertThat(activeToken.getExpiredAt()).isAfter(LocalDateTime.now());
        assertThat(activeTokenIsValid).isTrue();
    }
}
