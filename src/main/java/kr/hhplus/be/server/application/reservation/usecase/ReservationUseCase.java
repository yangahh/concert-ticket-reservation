package kr.hhplus.be.server.application.reservation.usecase;

import kr.hhplus.be.server.domain.concert.service.ConcertService;
import kr.hhplus.be.server.domain.reservation.dto.ReservationResult;
import kr.hhplus.be.server.domain.reservation.service.ReservationService;
import kr.hhplus.be.server.utils.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ReservationUseCase {
    private final ReservationService reservationService;
    private final ConcertService concertService;
    private final TimeProvider timeProvider;

    @Transactional
    public ReservationResult makeTempReservation(Long userId, Long seatId) {
        LocalDateTime now = timeProvider.now();
        concertService.reserveSeat(seatId, now);
        return reservationService.makeTempReservation(userId, seatId, now.plusMinutes(5));
    }
}
