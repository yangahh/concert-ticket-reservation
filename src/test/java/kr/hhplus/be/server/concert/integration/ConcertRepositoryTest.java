package kr.hhplus.be.server.concert.integration;

import kr.hhplus.be.server.domain.concert.entity.Concert;
import kr.hhplus.be.server.domain.concert.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.entity.Seat;
import kr.hhplus.be.server.domain.concert.repository.ConcertRepository;
import kr.hhplus.be.server.infrastructure.concert.repository.ConcertJpaRepository;
import kr.hhplus.be.server.infrastructure.concert.repository.ConcertScheduleJpaRepository;
import kr.hhplus.be.server.infrastructure.concert.repository.SeatJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ConcertRepositoryTest {

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private ConcertJpaRepository concertJpaRepository;

    @Autowired
    private ConcertScheduleJpaRepository concertScheduleJpaRepository;

    @Autowired
    private SeatJpaRepository seatJpaRepository;

    @Test
    @DisplayName("콘서트를 ID로 조회하면 해당 콘서트가 반환된다.")
    void findConcertById_shouldReturnConcert() {
        // given
        Long concertId = 1L;

        // when
        Optional<Concert> concert = concertRepository.findConcertById(concertId);

        // then
        assertThat(concert).isPresent();
        assertThat(concert.get().getId()).isEqualTo(concertId);
    }

    @Test
    @DisplayName("콘서트 일정을 ID로 조회하면 해당 일정이 반환된다.")
    void findConcertScheduleById_shouldReturnConcertSchedule() {
        // given
        Long concertScheduleId = 1L;

        // when
        Optional<ConcertSchedule> concertSchedule = concertRepository.findConcertScheduleById(concertScheduleId);

        // then
        assertThat(concertSchedule).isPresent();
        assertThat(concertSchedule.get().getId()).isEqualTo(concertScheduleId);
    }

    @Test
    @DisplayName("좌석을 ID로 조회하면 해당 좌석이 반환된다.")
    void findSeatById_shouldReturnSeat() {
        // given
        Long seatId = 1L;

        // when
        Optional<Seat> seat = concertRepository.findSeatById(seatId);

        // then
        assertThat(seat).isPresent();
        assertThat(seat.get().getId()).isEqualTo(seatId);
    }

    @Test
    @DisplayName("특정 콘서트의 일정 목록을 조회하면 해당 일정들이 반환된다.")
    void findConcertSchedulesByConcertId_shouldReturnSchedules() {
        // given
        Long concertId = 1L;
        int offset = 0;
        int limit = 10;

        // when
        Page<ConcertSchedule> schedules = concertRepository.findConcertSchedulesByConcertId(concertId, offset, limit);

        // then
        assertThat(schedules).isNotEmpty();
        assertThat(schedules.getContent()).hasSizeLessThanOrEqualTo(10); // data.sql 기준으로 3개의 일정
    }

    @Test
    @DisplayName("특정 콘서트 일정의 좌석 목록을 조회하면 해당 좌석들이 반환된다.")
    void findSeatsByConcertSchedule_shouldReturnSeats() {
        // given
        Long concertId = 1L;
        LocalDateTime concertDateTime = LocalDateTime.of(2025, 1, 15, 19, 0);
        int offset = 0;
        int limit = 50;

        // when
        Page<Seat> seats = concertRepository.findSeatsByConcertSchedule(concertId, concertDateTime, offset, limit);

        // then
        assertThat(seats).isNotEmpty();
        assertThat(seats.getContent()).hasSizeLessThanOrEqualTo(50);
    }
}