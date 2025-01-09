package kr.hhplus.be.server.domain.concert.service;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.common.exception.UnprocessableEntityException;
import kr.hhplus.be.server.domain.concert.dto.ConcertSchedulesResult;
import kr.hhplus.be.server.domain.concert.dto.ConcertSeatsResult;
import kr.hhplus.be.server.domain.concert.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.entity.Seat;
import kr.hhplus.be.server.domain.concert.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcertService {
    private final ConcertRepository concertRepository;

    public ConcertSchedulesResult getConcertSchedules(
            Long concertId, Optional<Integer> offset, Optional<Integer> limit) {

        checkConcert(concertId);

        int offsetValue = offset.orElse(0);
        int limitValue = limit.orElse(10);
        Page<ConcertSchedule> result = concertRepository.findConcertSchedulesByConcertId(concertId, offsetValue, limitValue);
        return ConcertSchedulesResult.fromPage(result);
    }

    public ConcertSeatsResult getSeatsByConcertIdAndEventDate(
            Long concertId, LocalDateTime eventDate, Optional<Integer> offset, Optional<Integer> limit) {

        checkConcert(concertId);
        vaildateConcertDateTime(eventDate);

        int offsetValue = offset.orElse(0);
        int limitValue = limit.orElse(50);
        Page<Seat> result = concertRepository.findSeatsByConcertSchedule(concertId, eventDate, offsetValue, limitValue);
        return ConcertSeatsResult.fromPage(result);
    }

    private void checkConcert(Long concertId) {
        concertRepository.findConcertById(concertId)
                .orElseThrow(() -> new EntityNotFoundException("Concert not found"));
    }

    private void vaildateConcertDateTime(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now())) {
            throw new UnprocessableEntityException("The event date is past");
        }
    }

}