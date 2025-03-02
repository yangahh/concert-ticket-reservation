package kr.hhplus.be.server.tests.concert.integration;

import kr.hhplus.be.server.domain.concert.entity.Concert;
import kr.hhplus.be.server.domain.concert.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.entity.Seat;
import kr.hhplus.be.server.domain.concert.repository.ConcertRepository;
import kr.hhplus.be.server.infrastructure.repository.concert.ConcertJpaRepository;
import kr.hhplus.be.server.infrastructure.repository.concert.ConcertScheduleJpaRepository;
import kr.hhplus.be.server.infrastructure.repository.concert.SeatJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Sql("/sql/concert_data.sql")
class ConcertRepositoryTest {

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private ConcertJpaRepository concertJpaRepository;

    @Autowired
    private ConcertScheduleJpaRepository concertScheduleJpaRepository;

    @Autowired
    private SeatJpaRepository seatJpaRepository;

    @DisplayName("콘서트 목록 조회: 예약 오픈 일이 입력된 날짜 이후인 콘서트 목록이 정상적으로 반환된다")
    @Test
    void findConcertsAfterDate_shouldReturnConcerts() {
        // given
        LocalDate date = LocalDate.of(2025, 1, 1);
        int offset = 0;
        int limit = 10;
        // concert_data.sql 기준으로 2025/01/01 이후 2개의 콘서트

        // when
        Page<Concert> concerts = concertRepository.findConcertsAfterDate(date, offset, limit);

        // then
        assertThat(concerts.getTotalElements()).isEqualTo(2);
    }

    @DisplayName("콘서트 목록 조회: limit 값이 1이면 1개의 콘서트만 반환된다(페이징 테스트)")
    @Test
    void findConcertsAfterDate_shouldReturnOneConcertWhenLimitIs1() {
        // given
        LocalDate date = LocalDate.of(2025, 1, 1);
        int offset = 0;
        int limit = 1;

        // when
        Page<Concert> concerts = concertRepository.findConcertsAfterDate(date, offset, limit);

        // then
        assertThat(concerts.getContent().size()).isEqualTo(1);
    }

    @DisplayName("콘서트 목록 조회: 예약 오픈 날짜가 주어진 날짜 이후인 콘서트가 없으면 빈 목록을 반환한다.")
    @Test
    void findConcertsAfterDate_shouldReturnEmptyListWhenNoConcertsAfterDate() {
        // given
        LocalDate date = LocalDate.of(2050, 1, 1);
        int offset = 0;
        int limit = 10;

        // when
        Page<Concert> concerts = concertRepository.findConcertsAfterDate(date, offset, limit);

        // then
        assertThat(concerts.getContent()).isEmpty();
        assertThat(concerts.getTotalElements()).isEqualTo(0);
    }

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
        Optional<Seat> seat = concertRepository.findSeatByIdForUpdate(seatId);

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
        // concert_data.sql 기준으로 3개의 일정

        // when
        Page<ConcertSchedule> schedules = concertRepository.findConcertSchedulesByConcertId(concertId, offset, limit);

        // then
        assertThat(schedules).isNotEmpty();
        assertThat(schedules.getContent()).hasSizeLessThanOrEqualTo(10);
    }

    @Test
    @DisplayName("특정 콘서트 일정의 좌석 목록을 조회하면 해당 좌석들이 반환된다.")
    void findSeatsByConcertSchedule_shouldReturnSeats() {
        // given
        Long concertId = 1L;
        LocalDate searchDate = LocalDate.of(2025, 3, 30);
        int offset = 0;
        int limit = 50;
        // concert_data.sql 기준으로 2025/03/30에 100개의 좌석이 존재

        // when
        Page<Seat> seats = concertRepository.findSeatsByConcertSchedule(concertId, searchDate, offset, limit);

        // then
        assertThat(seats.getTotalElements()).isEqualTo(100);
        assertThat(seats.getContent()).hasSizeLessThanOrEqualTo(50);
    }
}
