package kr.hhplus.be.server.application.payment.usecase;

import kr.hhplus.be.server.domain.common.exception.UnprocessableEntityException;
import kr.hhplus.be.server.domain.concert.service.ConcertService;
import kr.hhplus.be.server.domain.point.service.PointService;
import kr.hhplus.be.server.domain.queuetoken.service.QueueTokenService;
import kr.hhplus.be.server.domain.reservation.dto.ReservationResult;
import kr.hhplus.be.server.domain.reservation.service.ReservationService;
import kr.hhplus.be.server.utils.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentUseCase {
    private final ConcertService concertSeatService;
    private final ReservationService reservationService;
    private final PointService pointService;
    private final QueueTokenService queueTokenService;
    private final TimeProvider timeProvider;

    @Transactional
    public ReservationResult payForReservation(long reservationId, UUID tokenUuid) {
        LocalDateTime now = timeProvider.now();
        try {
            boolean tempReservationExpired = reservationService.isTempReservationExpired(reservationId, now);
            if (tempReservationExpired) {
                handelExpiredReservation(reservationId);
                throw new UnprocessableEntityException("Temp reservation expired");
            }

            ReservationResult reservationResult = reservationService.confirmReservation(reservationId, now);
            pointService.usePoint(reservationResult.userId(), reservationResult.price(), reservationId);
            queueTokenService.deleteToken(reservationResult.concertScheduleResult().concertId(), tokenUuid);

            return reservationResult;

        } catch (ObjectOptimisticLockingFailureException e) {
            throw new UnprocessableEntityException("Reservation is not a temporary reservation (id = " + reservationId + ")");
        } catch (UnprocessableEntityException e) {
            if (e.getMessage().equals("Not enough point for use")) {
                reservationService.rollbackToTempReservation(reservationId);
            }
            throw e;
        }
    }

    public void handelExpiredReservation(long reservationId) {
        Long seatId = reservationService.getSeatIdByReservationId(reservationId);
        concertSeatService.releaseSeat(seatId);
        reservationService.cancelReservation(reservationId);

        log.warn("Temp reservation expired. reservationId: {}", reservationId);
    }

}


