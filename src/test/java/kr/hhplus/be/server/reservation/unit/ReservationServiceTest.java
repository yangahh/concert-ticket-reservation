package kr.hhplus.be.server.reservation.unit;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.concert.entity.Concert;
import kr.hhplus.be.server.domain.concert.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.entity.Seat;
import kr.hhplus.be.server.domain.concert.repository.ConcertRepository;
import kr.hhplus.be.server.domain.reservation.dto.ReservationResult;
import kr.hhplus.be.server.domain.reservation.entity.Reservation;
import kr.hhplus.be.server.domain.reservation.repository.ReservationRepository;
import kr.hhplus.be.server.domain.reservation.service.ReservationService;
import kr.hhplus.be.server.domain.reservation.vo.ReservationStatus;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {
    @InjectMocks
    private ReservationService sut;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ConcertRepository concertRepository;

    @DisplayName("임시 예약 요청 시, 존재하지 않는 userId로 요청하면 예외가 발생한다.")
    @Test
    void shouldThrowEntityNotFoundExceptionWhenNotExistingUserId() {
        // given
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sut.makeTempReservation(1L, 1L, LocalDateTime.now()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User not found");
    }

    @DisplayName("임시 예약 요청 시, 존재하지 않는 seatId로 요청하면 예외가 발생한다.")
    @Test
    void shouldThrowEntityNotFoundExceptionWhenNotExistingSeatId() {
        // given
        User user = User.create("test");
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(concertRepository.findSeatById(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sut.makeTempReservation(1L, 1L, LocalDateTime.now()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Seat not found");
    }

    @DisplayName("정상적으로 임시 예약 요청을 하면 ReservationResult를 반환한다.")
    @Test
    void shouldReturnReservationResultSuccessTest() {
        // given
        LocalDateTime now = LocalDateTime.now();
        long userId = 1L;
        long concertId = 10L;
        long concertScheduleId = 100L;
        long seatId = 1000L;
        long reservationId = 1001L;


        User user = User.create("test");
        Concert concert = Concert.create("concert1");
        ConcertSchedule concertSchedule = ConcertSchedule.create(concert, LocalDateTime.of(2025, 3, 10, 18, 0, 0), 50);
        Seat seat = Seat.create(concertSchedule, "A1", false, 10000, now);
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(concertRepository.findSeatById(anyLong())).willReturn(Optional.of(seat));

        Reservation reservation = Reservation.tempReserve(user, seat, now);
        Reservation mockReservation = spy(reservation);
        Seat mockSeat = spy(seat);
        ConcertSchedule mockConcertSchedule = spy(concertSchedule);
        Concert mockConcert = spy(concert);
        User mockUser = spy(user);

        doReturn(userId).when(mockUser).getId();
        doReturn(reservationId).when(mockReservation).getId();
        doReturn(seatId).when(mockSeat).getId();
        doReturn(concertScheduleId).when(mockConcertSchedule).getId();
        doReturn(concertId).when(mockConcert).getId();
        doReturn(mockUser).when(mockReservation).getUser();
        doReturn(mockSeat).when(mockReservation).getSeat();
        doReturn(mockConcertSchedule).when(mockSeat).getConcertSchedule();
        doReturn(mockConcert).when(mockConcertSchedule).getConcert();

        given(reservationRepository.save(any(Reservation.class))).willReturn(mockReservation);

        // when
        ReservationResult reservationResult = sut.makeTempReservation(userId, seatId, now);

        // then
        assertThat(reservationResult.reservationId()).isEqualTo(reservationId);
        assertThat(reservationResult.userId()).isEqualTo(userId);
        assertThat(reservationResult.concertScheduleResult().concertId()).isEqualTo(concertId);
        assertThat(reservationResult.concertScheduleResult().concertScheduleId()).isEqualTo(concertScheduleId);
        assertThat(reservationResult.concertSeatResult().seatId()).isEqualTo(seatId);
        assertThat(reservationResult.concertSeatResult().isAvailable()).isFalse();
        assertThat(reservationResult.status()).isEqualTo(ReservationStatus.PENDING_PAYMENT);
    }

    @DisplayName("임시 예약이 만료되었는지 확인할 때, 만료된 상태를 반환한다.")
    @Test
    void shouldReturnTrueWhenTempReservationIsExpired() {
        // given
        Long reservationId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Reservation reservation = mock(Reservation.class);

        given(reservationRepository.findByIdForUpdate(reservationId)).willReturn(Optional.of(reservation));
        given(reservation.isTempReservationExpired(now)).willReturn(true);

        // when
        boolean isExpired = sut.isTempReservationExpired(reservationId, now);

        // then
        assertThat(isExpired).isTrue();
        verify(reservationRepository).findByIdForUpdate(reservationId);
        verify(reservation).isTempReservationExpired(now);
    }

    @Test
    @DisplayName("임시 예약 확정(=결제) 시 예약이 존재하지 않으면 예외를 발생시킨다.")
    void shouldThrowExceptionWhenReservationNotFound() {
        // given
        Long reservationId = 1L;
        LocalDateTime now = LocalDateTime.now();

        given(reservationRepository.findByIdForUpdate(reservationId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sut.confirmReservation(reservationId, now))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Reservation not found");
        verify(reservationRepository).findByIdForUpdate(reservationId);
    }

    @Test
    @DisplayName("임시 예약 확정(=결제) 시 예약이 성공적으로 확정된다.")
    void shouldConfirmReservationSuccessfully() {
        // given
        LocalDateTime now = LocalDateTime.now();
        long userId = 1L;
        long concertId = 10L;
        long concertScheduleId = 100L;
        long seatId = 1000L;
        long reservationId = 1L;

        User user = User.create("test");
        Concert concert = Concert.create("concert1");
        ConcertSchedule concertSchedule = ConcertSchedule.create(concert, LocalDateTime.of(2025, 3, 10, 18, 0, 0), 50);
        Seat seat = Seat.create(concertSchedule, "A1", false, 10000, now);

        Reservation reservation = Reservation.tempReserve(user, seat, now);
        Reservation mockReservation = spy(reservation);
        Seat mockSeat = spy(seat);
        ConcertSchedule mockConcertSchedule = spy(concertSchedule);
        Concert mockConcert = spy(concert);
        User mockUser = spy(user);

        doReturn(userId).when(mockUser).getId();
        doReturn(reservationId).when(mockReservation).getId();
        doReturn(seatId).when(mockSeat).getId();
        doReturn(concertScheduleId).when(mockConcertSchedule).getId();
        doReturn(concertId).when(mockConcert).getId();
        doReturn(mockUser).when(mockReservation).getUser();
        doReturn(mockSeat).when(mockReservation).getSeat();
        doReturn(mockConcertSchedule).when(mockSeat).getConcertSchedule();
        doReturn(mockConcert).when(mockConcertSchedule).getConcert();
        doReturn(ReservationStatus.CONFIRMED).when(mockReservation).getStatus();

        given(reservationRepository.findByIdForUpdate(reservationId)).willReturn(Optional.of(mockReservation));
        given(reservationRepository.save(any(Reservation.class))).willReturn(mockReservation);

        // when
        ReservationResult result = sut.confirmReservation(reservationId, now);

        // then
        assertThat(result.status()).isEqualTo(ReservationStatus.CONFIRMED);
        verify(reservationRepository).findByIdForUpdate(reservationId);
        verify(mockReservation).confirm(now);
        verify(reservationRepository).save(mockReservation);
    }

}
