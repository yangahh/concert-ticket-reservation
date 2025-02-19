package kr.hhplus.be.server.tests.reservation.integration;

import kr.hhplus.be.server.application.reservation.usecase.ReservationUseCase;
import kr.hhplus.be.server.domain.concert.entity.Concert;
import kr.hhplus.be.server.domain.concert.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.entity.Seat;
import kr.hhplus.be.server.domain.concert.service.ConcertService;
import kr.hhplus.be.server.domain.reservation.dto.ReservationResult;
import kr.hhplus.be.server.domain.reservation.service.ReservationService;
import kr.hhplus.be.server.domain.reservation.vo.ReservationStatus;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.infrastructure.repository.concert.ConcertJpaRepository;
import kr.hhplus.be.server.infrastructure.repository.concert.ConcertScheduleJpaRepository;
import kr.hhplus.be.server.infrastructure.repository.concert.SeatJpaRepository;
import kr.hhplus.be.server.infrastructure.repository.reservation.ReservationJpaRepository;
import kr.hhplus.be.server.infrastructure.repository.user.UserJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@SpringBootTest
public class ReservationUseCaseTest {
    @Autowired
    private ReservationUseCase reservationUsecase;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ConcertService concertService;

    @Autowired
    private SeatJpaRepository seatJpaRepository;

    @Autowired
    private ReservationJpaRepository reservationJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private ConcertJpaRepository concertJpaRepository;

    @Autowired
    private ConcertScheduleJpaRepository concertScheduleJpaRepository;


    User user;
    Seat seat;

    @BeforeEach
    void setUp() {
        user = userJpaRepository.save(User.create("test"));
        Concert concert = concertJpaRepository.save(Concert.create("test", LocalDateTime.now()));
        ConcertSchedule concertSchedule = concertScheduleJpaRepository.save(ConcertSchedule.create(concert, LocalDateTime.now().plusDays(1), 50));
        seat = seatJpaRepository.save(Seat.create(concertSchedule, "1", true, 1000, LocalDateTime.now().plusMinutes(5)));

    }

    @AfterEach
    void tearDown() {
        reservationJpaRepository.deleteAll();
    }

    @DisplayName("예약 요청 usecase 성공 테스트")
    @Test
    void Test() {
        // when
        ReservationResult reservationResult = reservationUsecase.makeTempReservation(user.getId(), seat.getId());

        // then
        Seat afterSeat = seatJpaRepository.findById(seat.getId()).get();
        assertThat(reservationResult.status()).isEqualTo(ReservationStatus.PENDING_PAYMENT);
        assertThat(afterSeat.getIsAvailable()).isFalse();
        assertThat(reservationResult.tempReservationExpiredAt()).isCloseTo(LocalDateTime.now().plusMinutes(5),  within(3, SECONDS));
    }

}
