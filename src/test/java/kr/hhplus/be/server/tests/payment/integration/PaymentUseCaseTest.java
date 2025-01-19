package kr.hhplus.be.server.tests.payment.integration;

import kr.hhplus.be.server.application.payment.usecase.PaymentUseCase;
import kr.hhplus.be.server.domain.common.exception.UnprocessableEntityException;
import kr.hhplus.be.server.domain.concert.entity.Seat;
import kr.hhplus.be.server.domain.point.entity.Point;
import kr.hhplus.be.server.domain.point.entity.PointHistory;
import kr.hhplus.be.server.domain.queuetoken.entity.QueueToken;
import kr.hhplus.be.server.domain.queuetoken.service.QueueTokenService;
import kr.hhplus.be.server.domain.reservation.dto.ReservationResult;
import kr.hhplus.be.server.domain.reservation.entity.Reservation;
import kr.hhplus.be.server.domain.reservation.vo.ReservationStatus;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.infrastructure.concert.repository.SeatJpaRepository;
import kr.hhplus.be.server.infrastructure.point.repository.PointHistoryJpaRepository;
import kr.hhplus.be.server.infrastructure.point.repository.PointJpaRepository;
import kr.hhplus.be.server.infrastructure.queuetoken.repository.QueueTokenJpaRepository;
import kr.hhplus.be.server.infrastructure.reservation.repository.ReservationJpaRepository;
import kr.hhplus.be.server.infrastructure.user.repository.UserJpaRepository;
import kr.hhplus.be.server.utils.time.TimeProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@SpringBootTest
@Transactional
public class PaymentUseCaseTest {
    @Autowired
    private PaymentUseCase paymentUseCase;

    @Autowired
    private QueueTokenService queueTokenService;

    @Autowired
    private QueueTokenJpaRepository queueTokenJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private ReservationJpaRepository reservationJpaRepository;

    @Autowired
    private SeatJpaRepository seatJpaRepository;

    @Autowired
    private PointJpaRepository pointJpaRepository;

    @MockitoBean
    private PointHistoryJpaRepository pointHistoryJpaRepository;

    @Autowired
    private TimeProvider timeProvider;

    User user;
    QueueToken token;
    Point userPoint;

    @BeforeEach
    void setUp() {
        token = QueueToken.createWaitingToken(1L, 1L, timeProvider);
        token.activate();
        queueTokenJpaRepository.save(token);
        user = userJpaRepository.findById(1L).get();
        userPoint = Point.create(user);
        userPoint.plus(1_000_000);
        userPoint = pointJpaRepository.save(userPoint);
    }

    @AfterEach
    void tearDown() {
        pointHistoryJpaRepository.deleteAll();
        pointJpaRepository.deleteAll();
        queueTokenJpaRepository.deleteAll();
        reservationJpaRepository.deleteAll();
    }

    @DisplayName("결제 성공")
    @Test
    void payForReservationSuccessTest() {
        // given
        Seat seat = seatJpaRepository.findById(1L).get();
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
        assertThat(queueTokenJpaRepository.findByTokenUuid(token.getTokenUuid())).isEmpty();
    }

    @DisplayName("해당 임시 예약이 만료된 경우 결제에 실패하고, 좌석 상태 및 예약 상태가 변경된다.")
    @Test
    void shouldFailToPayForReservationWhenTempReservationExpired() {
        // given
        Seat seat = seatJpaRepository.findById(2L).get();
        seat.reserve(timeProvider.now());
        seat = seatJpaRepository.save(seat);
        Reservation tempReservation = reservationJpaRepository.save(Reservation.tempReserve(user, seat, timeProvider.now().minusMinutes(5)));

        // when & then
        assertThatThrownBy(() -> paymentUseCase.payForReservation(tempReservation.getId(), token.getTokenUuid()))
            .isInstanceOf(UnprocessableEntityException.class)
            .hasMessage("Temp reservation expired");

        Seat afterSeat = seatJpaRepository.findById(2L).get();
        assertThat(afterSeat.getIsAvailable()).isTrue();
        assertThat(afterSeat.getTempReservationExpiredAt()).isNull();
        assertThat(reservationJpaRepository.findById(tempReservation.getId()).get().getStatus()).isEqualTo(ReservationStatus.CANCELED);
        then(pointHistoryJpaRepository).should((never())).save(any(PointHistory.class));
    }
}