package kr.hhplus.be.server.point.unit;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.common.exception.UnprocessableEntityException;
import kr.hhplus.be.server.domain.point.dto.PointResult;
import kr.hhplus.be.server.domain.point.entity.Point;
import kr.hhplus.be.server.domain.point.entity.PointHistory;
import kr.hhplus.be.server.domain.point.repository.PointRepository;
import kr.hhplus.be.server.domain.point.service.PointService;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {
    @InjectMocks
    private PointService sut;

    @Mock
    private PointRepository pointRepository;

    @Mock
    private UserRepository userRepository;

    @DisplayName("포인트 조회: 존재하지 않는 유저 id로 조회하면 예외가 발생한다.")
    @Test
    void getUserPoint_shouldThrowEntityNotFoundExceptionWithNotExistingUserId() {
        // given
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        // when  // then
        assertThatThrownBy(() -> sut.getUserPoint(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User not found");
    }

    @DisplayName("포인트 조회: 포인트 데이터가 없는 유저일 경우에는 초기화된 포인트를 반환한다.")
    @Test
    void getUserPoint_shouldReturnInitializedPointWhenNoPointData() {
        // given
        given(pointRepository.findByUserId(1L)).willReturn(Optional.empty());
        given(userRepository.findById(1L)).willReturn(Optional.of(User.create("test")));

        // when
        PointResult result = sut.getUserPoint(1L);

        // then
        assertThat(result.balance()).isEqualTo(0);
    }

    @DisplayName("포인트 조회: 이미 존재하는 포인트 데이터가 있는 유저일 경우에는 해당 포인트 데이터를 반환한다.")
    @Test
    void getUserPoint_shouldReturnExistingPoint() {
        // given
        User user = User.create("test");
        Point point = Point.create(user);
        point.plus(100);
        given(pointRepository.findByUserId(1L)).willReturn(Optional.of(point));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // when
        PointResult result = sut.getUserPoint(1L);

        // then
        assertThat(result.balance()).isEqualTo(100);
    }

    @DisplayName("포인트 충전: 포인터 데이터가 없는 사용자일 경우에 초기화된 포인트에 정상적으로 충전하고 충전 내역을 기록한다.")
    @Test
    void chargePoint_shouldChargePointAndRecordHistoryWhenNoPointData() {
        // given
        long userId = 1L;
        int amount = 100;
        User user = User.create("test");
        given(pointRepository.findByUserId(userId)).willReturn(Optional.empty());
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        Point expectedPoint = Point.create(user);
        expectedPoint.plus(amount);
        given(pointRepository.savePoint(any(Point.class))).willReturn(expectedPoint);

        // when
        PointResult result = sut.chargePoint(userId, amount);

        // then
        assertThat(result.balance()).isEqualTo(100);
        then(pointRepository).should().savePointHistory(any(PointHistory.class));

    }

    @DisplayName("포인트 충전: 이미 포인터 데이터가 있는 사용자가 요청하면 정상적으로 포인트를 충전하고 충전 내역을 기록한다.")
    @Test
    void chargePoint_shouldChargePointAndRecordHistoryWhenExistingPoint() {
        // given
        long userId = 1L;
        int amount = 100;
        int balance = 1000;
        User user = User.create("test");
        Point point = Point.create(user);
        point.plus(balance);
        given(pointRepository.findByUserId(userId)).willReturn(Optional.of(point));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        Point expectedPoint = Point.create(user);
        expectedPoint.plus(balance + amount);
        given(pointRepository.savePoint(any(Point.class))).willReturn(expectedPoint);

        // when
        PointResult result = sut.chargePoint(userId, amount);

        // then
        assertThat(result.balance()).isEqualTo(1100);
        then(pointRepository).should().savePointHistory(any(PointHistory.class));

    }

    @DisplayName("포인트 사용: 포인터 데이터가 없는 사용자일 경우에는 예외가 발생한다.")
    @Test
    void usePoint_shouldThrowEntityNotFoundExceptionWhenNoPointData() {
        // given
        long userId = 1L;
        int amount = 100;
        User user = User.create("test");
        given(pointRepository.findByUserId(userId)).willReturn(Optional.empty());

        // when // then
        assertThatThrownBy(() -> sut.usePoint(userId, amount, null))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Point not found");
    }

    @DisplayName("포인트 사용: 포인트를 사용하려는 금액보다 가지고 있는 포인트가 적을 경우에는 예외가 발생한다.")
    @Test
    void usePoint_shouldThrowEntityNotFoundExceptionWhenNotEnoughPoint() {
        // given
        long userId = 1L;
        int amount = 2000;
        int balance = 1000;
        User user = User.create("test");
        Point point = Point.create(user);
        point.plus(balance);
        given(pointRepository.findByUserId(userId)).willReturn(Optional.of(point));

        // when // then
        assertThatThrownBy(() -> sut.usePoint(userId, amount, null))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessage("Not enough point for use");

    }

    @DisplayName("포인트 사용: 포인트를 정상적으로 사용하고 사용 내역을 기록한다.")
    @Test
    void usePoint_shouldUsePointAndRecordHistory() {
        // given
        long userId = 1L;
        int amount = 100;
        int balance = 1000;
        User user = User.create("test");
        Point point = Point.create(user);
        point.plus(balance);
        given(pointRepository.findByUserId(userId)).willReturn(Optional.of(point));

        Point expectedPoint = Point.create(user);
        expectedPoint.plus(balance - amount);
        given(pointRepository.savePoint(any(Point.class))).willReturn(expectedPoint);

        // when
        PointResult result = sut.usePoint(userId, amount, null);

        // then
        assertThat(result.balance()).isEqualTo(900);
        then(pointRepository).should().savePointHistory(any(PointHistory.class));
    }

}
