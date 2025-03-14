package kr.hhplus.be.server.tests.reservation.unit;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.concert.dto.ReservationSeatInfo;
import kr.hhplus.be.server.domain.concert.entity.*;
import kr.hhplus.be.server.domain.concert.repository.ConcertRepository;
import kr.hhplus.be.server.domain.reservation.dto.ReservationResult;
import kr.hhplus.be.server.domain.reservation.entity.Reservation;
import kr.hhplus.be.server.domain.reservation.repository.ReservationRepository;
import kr.hhplus.be.server.domain.reservation.service.ReservationService;
import kr.hhplus.be.server.domain.reservation.vo.ReservationStatus;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

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
        Concert concert = Concert.create("concert1", LocalDateTime.now());
        ConcertSchedule concertSchedule = ConcertSchedule.create(concert, LocalDateTime.of(2025, 3, 10, 18, 0, 0), 50);
        Seat seat = Seat.create(concertSchedule, "A1", false, 10000, LocalDateTime.now());
        ReservationSeatInfo reservationSeatInfo = ReservationSeatInfo.fromEntity(seat);

        // when & then
        assertThatThrownBy(() -> sut.makeTempReservation(1L, seat.getId(), LocalDateTime.now()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @MockitoSettings(strictness = Strictness.LENIENT)
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
        Concert concert = Concert.create("concert1", LocalDateTime.now());
        ConcertSchedule concertSchedule = ConcertSchedule.create(concert, LocalDateTime.of(2025, 3, 10, 18, 0, 0), 50);
        Seat seat = Seat.create(concertSchedule, "A1", false, 10000, now);
        Reservation reservation = Reservation.tempReserve(user, seat, now);

        Reservation mockReservation = spy(reservation);
        Seat mockSeat = spy(seat);
        ConcertSchedule mockConcertSchedule = spy(concertSchedule);
        Concert mockConcert = spy(concert);
        User mockUser = spy(user);

        // set id
        doReturn(userId).when(mockUser).getId();
        doReturn(reservationId).when(mockReservation).getId();
        doReturn(seatId).when(mockSeat).getId();
        doReturn(10000).when(mockSeat).getPrice();
        doReturn(concertScheduleId).when(mockConcertSchedule).getId();
        doReturn(concertId).when(mockConcert).getId();

        // set relation
        doReturn(mockUser).when(mockReservation).getUser();
        doReturn(mockSeat).when(mockReservation).getSeat();
        doReturn(mockConcertSchedule).when(mockSeat).getConcertSchedule();
        doReturn(mockConcert).when(mockConcertSchedule).getConcert();

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(concertRepository.findSeatById(anyLong())).willReturn(Optional.of(seat));
        given(reservationRepository.save(any(Reservation.class))).willReturn(mockReservation);
        given(concertRepository.getReferenceSeatById(seatId)).willReturn(mockSeat);
        ReservationSeatInfo seatInfo = ReservationSeatInfo.fromEntity(mockSeat);

        // when
        ReservationResult reservationResult = sut.makeTempReservation(userId, seatInfo.seatId(), now);

        // then
        assertThat(reservationResult.reservationId()).isEqualTo(reservationId);
        assertThat(reservationResult.userId()).isEqualTo(userId);
        assertThat(reservationResult.concertScheduleResult().concertId()).isEqualTo(concertId);
        assertThat(reservationResult.concertScheduleResult().concertScheduleId()).isEqualTo(concertScheduleId);
        assertThat(reservationResult.concertSeatResult().seatId()).isEqualTo(seatId);
        assertThat(reservationResult.concertSeatResult().isAvailable()).isFalse();
        assertThat(reservationResult.status()).isEqualTo(ReservationStatus.PENDING_PAYMENT);
    }


    @Test
    @DisplayName("임시 예약 확정(=결제) 시 예약이 존재하지 않으면 예외를 발생시킨다.")
    void shouldThrowExceptionWhenReservationNotFound() {
        // given
        Long reservationId = 1L;
        LocalDateTime now = LocalDateTime.now();

        given(reservationRepository.findById(reservationId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sut.confirmReservation(reservationId, now))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Reservation not found");
        verify(reservationRepository).findById(reservationId);
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
        Concert concert = Concert.create("concert1", LocalDateTime.now());
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

        given(reservationRepository.findById(reservationId)).willReturn(Optional.of(mockReservation));
        given(reservationRepository.save(any(Reservation.class))).willReturn(mockReservation);

        // when
        ReservationResult result = sut.confirmReservation(reservationId, now);

        // then
        assertThat(result.status()).isEqualTo(ReservationStatus.CONFIRMED);
        verify(reservationRepository).findById(reservationId);
        verify(mockReservation).confirm(now);
        verify(reservationRepository).save(mockReservation);
    }

}
