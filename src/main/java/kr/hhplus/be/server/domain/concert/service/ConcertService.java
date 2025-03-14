package kr.hhplus.be.server.domain.concert.service;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.common.exception.UnprocessableEntityException;
import kr.hhplus.be.server.domain.concert.dto.ConcertSchedulesResult;
import kr.hhplus.be.server.domain.concert.dto.ConcertSeatsResult;
import kr.hhplus.be.server.domain.concert.dto.ConcertsResult;
import kr.hhplus.be.server.domain.concert.dto.ReservationSeatInfo;
import kr.hhplus.be.server.domain.concert.entity.Concert;
import kr.hhplus.be.server.domain.concert.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.entity.Seat;
import kr.hhplus.be.server.domain.concert.repository.ConcertRepository;
import kr.hhplus.be.server.utils.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcertService {
    private final ConcertRepository concertRepository;
    private final TimeProvider timeProvider;

    @Cacheable(value = "getConcertsAfterDate", key = "'concert:' + #date + ':offset:' + #offset + ':limit:' + #limit", cacheManager = "concertCacheManager")
    public ConcertsResult getConcertsAfterDate(LocalDate date, int offset, int limit) {
        return ConcertsResult.fromPage(concertRepository.findConcertsAfterDate(date, offset, limit));
    }

    public List<Long> getAllConcertIds() {
        List<Concert> allConcert = concertRepository.findAllConcert();
        return allConcert.stream()
                .map(Concert::getId)
                .toList();
    }

    public ConcertSchedulesResult getConcertSchedules(
            Long concertId, Optional<Integer> offset, Optional<Integer> limit) {

        checkConcert(concertId);

        int offsetValue = offset.orElse(0);
        int limitValue = limit.orElse(10);
        Page<ConcertSchedule> result = concertRepository.findConcertSchedulesByConcertId(concertId, offsetValue, limitValue);
        return ConcertSchedulesResult.fromPage(result);
    }

    public ConcertSeatsResult getSeatsByConcertIdAndEventDate(
        Long concertId, LocalDate searchDate, Optional<Integer> offset, Optional<Integer> limit) {

        checkConcert(concertId);
        validateConcertDateTime(searchDate);

        int offsetValue = offset.orElse(0);
        int limitValue = limit.orElse(50);
        Page<Seat> result = concertRepository.findSeatsByConcertSchedule(concertId, searchDate, offsetValue, limitValue);
        return ConcertSeatsResult.fromPage(result);
    }

    private void checkConcert(Long concertId) {
        concertRepository.findConcertById(concertId)
                .orElseThrow(() -> new EntityNotFoundException("Concert not found (id = " + concertId + ")"));
    }

    private void validateConcertDateTime(LocalDate searchDate) {
        if (searchDate.isBefore(timeProvider.now().toLocalDate())) {
            throw new UnprocessableEntityException("The event date is past");
        }
    }

    @Transactional
    public void releaseSeats(List<Long> seatIds) {
        concertRepository.updateSeatsToAvailableByIds(seatIds);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void releaseSeat(Long seatId) {
        concertRepository.updateSeatToAvailableById(seatId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ReservationSeatInfo reserveSeat(Long seatId, LocalDateTime now) {
        Seat seat = concertRepository.findSeatByIdForUpdate(seatId)
            .orElseThrow(() -> new EntityNotFoundException("Seat not found (id = " + seatId + ")"));

        seat.reserve(now);
        Seat reservedSeat = concertRepository.saveSeat(seat);
        return ReservationSeatInfo.fromEntity(reservedSeat);
    }
}
