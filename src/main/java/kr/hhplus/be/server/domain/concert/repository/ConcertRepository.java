package kr.hhplus.be.server.domain.concert.repository;

import kr.hhplus.be.server.domain.concert.entity.Concert;
import kr.hhplus.be.server.domain.concert.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.entity.Seat;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ConcertRepository {
    Optional<Concert> findConcertById(Long concertId);

    Optional<ConcertSchedule> findConcertScheduleById(Long concertScheduleId);

    Optional<Seat> findSeatById(Long seatId);

    Page<ConcertSchedule> findConcertSchedulesByConcertId(Long concertId, int offset, int limit);

    Page<Seat> findSeatsByConcertSchedule(Long concertId, LocalDateTime concertDateTime, int offset, int limit);
}
