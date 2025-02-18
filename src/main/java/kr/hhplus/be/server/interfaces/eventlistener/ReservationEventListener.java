package kr.hhplus.be.server.interfaces.eventlistener;

import kr.hhplus.be.server.domain.reservation.event.ReservationExpiredEvent;
import kr.hhplus.be.server.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationEventListener {
    private final ReservationService reservationService;

    @Retryable(
        retryFor = ObjectOptimisticLockingFailureException.class,
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handleExpiredReservationEvent(ReservationExpiredEvent event) {
        try {
            log.info("===========만료된 예약 취소 처리: 예약 ID = {}===========", event.reservationId());
            reservationService.handelExpiredReservation(event.reservationId());
            log.info("===========만료된 예약 취소 처리 완료===========");
        } catch (Exception e) {
            log.error(": {}", e.getMessage());
        }
    }
}
