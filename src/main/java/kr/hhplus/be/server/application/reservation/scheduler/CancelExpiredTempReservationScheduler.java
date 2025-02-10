package kr.hhplus.be.server.application.reservation.scheduler;

import kr.hhplus.be.server.domain.concert.service.ConcertService;
import kr.hhplus.be.server.domain.reservation.service.ReservationService;
import kr.hhplus.be.server.utils.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CancelExpiredTempReservationScheduler {
    private final ReservationService reservationService;
    private final ConcertService seatService;
    private final TimeProvider timeProvider;

    @Scheduled(fixedRate = 10000) // 10초마다 실행
    @Transactional
    public void handleExpiredReservations() {
        LocalDateTime now = timeProvider.now();
        log.info("==============================CancelExpiredTempReservations================================");
        log.info("Started at {}", now);
        List<Long> expiredSeatIds = reservationService.cancelExpiredTempReservations(now);

        if (expiredSeatIds.isEmpty()) {
            log.info("No expired reservations");
        } else {
            seatService.releaseSeats(expiredSeatIds);
            log.info("Released {} seats", expiredSeatIds.size());
        }
        log.info("==============================CancelExpiredTempReservations End================================");
    }
}
