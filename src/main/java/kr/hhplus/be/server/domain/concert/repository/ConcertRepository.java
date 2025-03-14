package kr.hhplus.be.server.domain.concert.repository;

import kr.hhplus.be.server.domain.concert.entity.Concert;
import kr.hhplus.be.server.domain.concert.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.entity.Seat;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ConcertRepository {
    Page<Concert> findConcertsAfterDate(LocalDate date, int offset, int limit);

    List<Concert> findAllConcert();

    Optional<Concert> findConcertById(Long concertId);

    Optional<ConcertSchedule> findConcertScheduleById(Long concertScheduleId);

    Optional<Seat> findSeatByIdForUpdate(Long seatId);

    Optional<Seat> findSeatById(Long seatId);

    Page<ConcertSchedule> findConcertSchedulesByConcertId(Long concertId, int offset, int limit);

    Page<Seat> findSeatsByConcertSchedule(Long concertId, LocalDate searchDate, int offset, int limit);

    Seat saveSeat(Seat seat);

    void updateSeatsToAvailableByIds(List<Long> seatIds);

    void updateSeatToAvailableById(Long seatId);

    Seat getReferenceSeatById(Long seatId);
}
