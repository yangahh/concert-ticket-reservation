package kr.hhplus.be.server.tests.reservation.integration;

import kr.hhplus.be.server.application.reservation.usecase.ReservationUseCase;
import kr.hhplus.be.server.domain.concert.entity.Seat;
import kr.hhplus.be.server.domain.concert.service.ConcertService;
import kr.hhplus.be.server.domain.reservation.dto.ReservationResult;
import kr.hhplus.be.server.domain.reservation.service.ReservationService;
import kr.hhplus.be.server.domain.reservation.vo.ReservationStatus;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.infrastructure.concert.repository.ConcertJpaRepository;
import kr.hhplus.be.server.infrastructure.concert.repository.ConcertScheduleJpaRepository;
import kr.hhplus.be.server.infrastructure.concert.repository.SeatJpaRepository;
import kr.hhplus.be.server.infrastructure.reservation.repository.ReservationJpaRepository;
import kr.hhplus.be.server.infrastructure.user.repository.UserJpaRepository;
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
public class ReservationUsecaseTest {
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

    User user;
    Seat seat;

    @BeforeEach
    void setUp() {
        user = userJpaRepository.findById(1L).get();  //data.sql에 미리 저장된 사용자
        seat = seatJpaRepository.findById(1L).get();
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
