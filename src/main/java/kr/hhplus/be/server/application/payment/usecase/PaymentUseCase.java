package kr.hhplus.be.server.application.payment.usecase;

import kr.hhplus.be.server.application.payment.dto.PaymentCompletedCriteria;
import kr.hhplus.be.server.domain.payment.dto.PaymentResult;
import kr.hhplus.be.server.domain.payment.event.PaymentCompletedEvent;
import kr.hhplus.be.server.domain.payment.service.PaymentService;
import kr.hhplus.be.server.domain.point.service.PointService;
import kr.hhplus.be.server.domain.queuetoken.service.QueueTokenService;
import kr.hhplus.be.server.domain.reservation.dto.ReservationResult;
import kr.hhplus.be.server.domain.reservation.service.ReservationService;
import kr.hhplus.be.server.utils.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentUseCase {
    private final PaymentService paymentService;
    private final ReservationService reservationService;
    private final PointService pointService;
    private final QueueTokenService queueTokenService;
    private final TimeProvider timeProvider;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public ReservationResult payForReservation(long reservationId, UUID tokenUuid) {
        ReservationResult reservationResult = reservationService.confirmReservation(reservationId, timeProvider.now());
        PaymentResult paymentResult = paymentService.createPayment(reservationResult);
        pointService.usePoint(reservationResult.userId(), reservationResult.price(), reservationId);
        queueTokenService.deleteToken(reservationResult.concertScheduleResult().concertId(), tokenUuid);

        applicationEventPublisher.publishEvent(PaymentCompletedEvent.from(PaymentCompletedCriteria.of(paymentResult, reservationResult)));
        return reservationResult;
    }
}


