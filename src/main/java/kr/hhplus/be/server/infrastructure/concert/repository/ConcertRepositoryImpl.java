package kr.hhplus.be.server.infrastructure.concert.repository;

import kr.hhplus.be.server.domain.concert.entity.Concert;
import kr.hhplus.be.server.domain.concert.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.entity.Seat;
import kr.hhplus.be.server.domain.concert.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepository {
    private final ConcertJpaRepository concertJpaRepository;
    private final ConcertScheduleJpaRepository concertScheduleJpaRepository;
    private final SeatJpaRepository seatJpaRepository;

    @Override
    public Optional<Concert> findConcertById(Long concertId) {
        return concertJpaRepository.findById(concertId);
    }

    @Override
    public Optional<ConcertSchedule> findConcertScheduleById(Long concertScheduleId) {
        return concertScheduleJpaRepository.findById(concertScheduleId);
    }

    @Override
    public Optional<Seat> findSeatByIdForUpdate(Long seatId) {
        return seatJpaRepository.findByIdWithLock(seatId);
    }

    @Override
    public Optional<Seat> findSeatById(Long seatId) {
        return seatJpaRepository.findById(seatId);
    }

    @Override
    public Page<ConcertSchedule> findConcertSchedulesByConcertId(Long concertId, int offset, int limit) {
        Pageable pageable = Pageable.ofSize(limit).withPage(offset/limit);
        return concertScheduleJpaRepository.findAllByConcertId(concertId, pageable);
    }

    @Override
    public Page<Seat> findSeatsByConcertSchedule(Long concertId, LocalDate searchDate, int offset, int limit) {

        LocalDateTime startDateTime = searchDate.atStartOfDay();
        LocalDateTime endDateTime = searchDate.atTime(LocalTime.MAX);

        Pageable pageable = Pageable.ofSize(limit).withPage(offset/limit);
        return seatJpaRepository.findAllByConcertIdAndEventDate(concertId, startDateTime, endDateTime, pageable);
    }

    @Override
    public Seat saveSeat(Seat seat) {
        return seatJpaRepository.save(seat);
    }

    @Override
    public void updateSeatsToAvailableByIds(List<Long> seatIds) {
        seatJpaRepository.updateByIdsToAvailable(seatIds);
    }

    @Override
    public void updateSeatToAvailableById(Long seatId) {
        seatJpaRepository.updateToAvailableById(seatId);
    }
}
