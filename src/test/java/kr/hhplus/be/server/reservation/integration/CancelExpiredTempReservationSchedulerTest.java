package kr.hhplus.be.server.reservation.integration;

import kr.hhplus.be.server.application.reservation.scheduler.CancelExpiredTempReservationScheduler;
import kr.hhplus.be.server.domain.concert.entity.Concert;
import kr.hhplus.be.server.domain.concert.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.entity.Seat;
import kr.hhplus.be.server.domain.concert.service.ConcertService;
import kr.hhplus.be.server.domain.reservation.entity.Reservation;
import kr.hhplus.be.server.domain.reservation.service.ReservationService;
import kr.hhplus.be.server.domain.reservation.vo.ReservationStatus;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.infrastructure.concert.repository.ConcertJpaRepository;
import kr.hhplus.be.server.infrastructure.concert.repository.ConcertScheduleJpaRepository;
import kr.hhplus.be.server.infrastructure.concert.repository.SeatJpaRepository;
import kr.hhplus.be.server.infrastructure.reservation.repository.ReservationJpaRepository;
import kr.hhplus.be.server.infrastructure.user.repository.UserJpaRepository;
import kr.hhplus.be.server.utils.time.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class CancelExpiredTempReservationSchedulerTest {
    @Autowired
    private CancelExpiredTempReservationScheduler scheduler;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ConcertService concertService;

    @Autowired
    private TimeProvider timeProvider;

    @Autowired
    private SeatJpaRepository seatJpaRepository;

    @Autowired
    private ReservationJpaRepository reservationJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private ConcertScheduleJpaRepository concertScheduleJpaRepository;

    @Autowired
    private ConcertJpaRepository concertJpaRepository;

    Seat expiredSeat;
    Reservation expiredReservation;

    Seat validSeat;
    Reservation validReservation;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        User user = userJpaRepository.findById(1L).get();  //data.sql에 미리 저장된 사용자
        Concert concert = concertJpaRepository.save(Concert.create("concert"));
        ConcertSchedule concertSchedule = concertScheduleJpaRepository.save(ConcertSchedule.create(concert, now.plusDays(2), 1000));

        // 만료된 임시 예약
        Seat seat1 = Seat.create(concertSchedule, "1", false, 1000, now.minusMinutes(10));
        expiredSeat = seatJpaRepository.save(seat1);
        Reservation expired = Reservation.tempReserve(user, seat1, now.minusMinutes(10));
        expiredReservation = reservationJpaRepository.save(expired);

        // 유효한 임시 예약
        Seat seat2 = Seat.create(concertSchedule, "1", false, 1000, now.plusMinutes(10));
        validSeat = seatJpaRepository.save(seat2);
        Reservation valid = Reservation.tempReserve(user, seat2, now.plusMinutes(10));
        validReservation = reservationJpaRepository.save(valid);

    }

    @DisplayName("임시 예약 만료 시간이 지난 예약 데이터와 해당 좌석 ")
    @Test
    void Test() {
        // when
        scheduler.handleExpiredReservations();

        // then
        Seat expiredSeat = seatJpaRepository.findById(this.expiredSeat.getId()).get();
        Reservation expiredReservation = reservationJpaRepository.findById(this.expiredReservation.getId()).get();

        assertThat(expiredSeat.getIsAvailable()).isTrue();
        assertThat(expiredSeat.getTempReservationExpiredAt()).isNull();
        assertThat(expiredReservation.getStatus()).isEqualTo(ReservationStatus.CANCELED);

        Seat validSeat = seatJpaRepository.findById(this.validSeat.getId()).get();
        Reservation validReservation = reservationJpaRepository.findById(this.validReservation.getId()).get();

        assertThat(validSeat.getIsAvailable()).isFalse();
        assertThat(validSeat.getTempReservationExpiredAt()).isNotNull();
        assertThat(validReservation.getStatus()).isEqualTo(ReservationStatus.PENDING_PAYMENT);
    }
}
