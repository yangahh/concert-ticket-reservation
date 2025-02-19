package kr.hhplus.be.server.tests.payment.integration;

import kr.hhplus.be.server.application.payment.usecase.PaymentUseCase;
import kr.hhplus.be.server.domain.common.exception.UnprocessableEntityException;
import kr.hhplus.be.server.domain.concert.entity.Concert;
import kr.hhplus.be.server.domain.concert.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.entity.Seat;
import kr.hhplus.be.server.domain.point.entity.Point;
import kr.hhplus.be.server.domain.point.entity.PointHistory;
import kr.hhplus.be.server.domain.queuetoken.entity.QueueToken;
import kr.hhplus.be.server.domain.queuetoken.service.QueueTokenService;
import kr.hhplus.be.server.domain.reservation.dto.ReservationResult;
import kr.hhplus.be.server.domain.reservation.entity.Reservation;
import kr.hhplus.be.server.domain.reservation.vo.ReservationStatus;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.infrastructure.repository.point.PointHistoryJpaRepository;
import kr.hhplus.be.server.tests.support.InfraRepositorySupport;
import kr.hhplus.be.server.utils.time.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@SpringBootTest
public class PaymentUseCaseTest extends InfraRepositorySupport {
    @Autowired
    private PaymentUseCase paymentUseCase;

    @Autowired
    private QueueTokenService queueTokenService;

    @MockitoBean
    private PointHistoryJpaRepository pointHistoryJpaRepository;

    @Autowired
    private TimeProvider timeProvider;

    User user;
    QueueToken token;
    Point userPoint;
    Concert concert;
    ConcertSchedule concertSchedule;


    @BeforeEach
    void setUp() {
        user = userJpaRepository.save(User.create("test"));
        token = QueueToken.createWaitingToken(user.getId(), 1L, timeProvider);
        token.activate();
        queueTokenRepository.save(token);

        userPoint = Point.create(user);
        userPoint.plus(1_000_000);
        userPoint = pointJpaRepository.save(userPoint);

        concert = concertJpaRepository.save(Concert.create("test", timeProvider.now()));
        concertSchedule = concertScheduleJpaRepository.save(ConcertSchedule.create(concert, timeProvider.now().plusDays(1), 50));
    }

    @DisplayName("정상적으로 저장된 예약에 대해서 유효시간 안에 결제 요청 시 결제에 성공한다.")
    @Test
    void payForReservationSuccessTest() {
        // given
        Seat seat = Seat.create(concertSchedule, "A", true, 10000, timeProvider.now().plusMinutes(5));
        seat.reserve(timeProvider.now());
        seat = seatJpaRepository.save(seat);
        Reservation tempReservation = reservationJpaRepository.save(Reservation.tempReserve(user, seat, timeProvider.now().plusMinutes(5)));

        // when
        ReservationResult reservationResult = paymentUseCase.payForReservation(tempReservation.getId(), token.getTokenUuid());

        // then
        // 예약 상태 확인, 좌석 상태 확인, 포인트 차감 확인, 포인트 내역 확인, 토큰 삭제 확인
        assertThat(reservationResult.status()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(reservationResult.concertSeatResult().isAvailable()).isFalse();
        assertThat(pointJpaRepository.findById(userPoint.getId()).get().getBalance()).isEqualTo(1_000_000 - seat.getPrice());
        then(pointHistoryJpaRepository).should().save(any(PointHistory.class));
        assertThat(queueTokenRepository.findByConcertIdAndTokenUuid(concert.getId(), token.getTokenUuid())).isEmpty();
    }

    @DisplayName("해당 임시 예약이 만료된 경우 결제에 실패하고, 좌석 상태 및 예약 상태가 변경된다.")
    @Test
    void shouldFailToPayForReservationWhenTempReservationExpired() throws InterruptedException {
        // given
        Seat seat = Seat.create(concertSchedule, "A", true, 10000, timeProvider.now().minusMinutes(5));
        seat.reserve(timeProvider.now());
        seat = seatJpaRepository.save(seat);
        Reservation tempReservation = reservationJpaRepository.save(Reservation.tempReserve(user, seat, timeProvider.now().minusMinutes(5)));

        // when & then
        assertThatThrownBy(() -> paymentUseCase.payForReservation(tempReservation.getId(), token.getTokenUuid()))
            .isInstanceOf(UnprocessableEntityException.class)
            .hasMessage("Temp reservation expired");

        Thread.sleep(100); // 이벤트 처리를 위한 대기

        Seat afterSeat = seatJpaRepository.findById(seat.getId()).get();
        assertThat(afterSeat.getIsAvailable()).isTrue();
        assertThat(afterSeat.getTempReservationExpiredAt()).isNull();
        assertThat(reservationJpaRepository.findById(tempReservation.getId()).get().getStatus()).isEqualTo(ReservationStatus.CANCELED);
        then(pointHistoryJpaRepository).should((never())).save(any(PointHistory.class));
    }
}
