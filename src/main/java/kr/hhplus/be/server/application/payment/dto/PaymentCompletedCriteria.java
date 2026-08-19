package kr.hhplus.be.server.application.payment.dto;

import kr.hhplus.be.server.domain.payment.dto.PaymentResult;
import kr.hhplus.be.server.domain.payment.event.PaymentCompletedEvent;
import kr.hhplus.be.server.domain.reservation.dto.ReservationResult;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PaymentCompletedCriteria (
    Long paymentId,
    Long userId,
    Long reservationId,
    Long concertId,
    Long concertScheduleId,
    Long seatId,
    int paymentAmount,
    LocalDateTime paymentAt
) {
    public static PaymentCompletedCriteria of (PaymentResult paymentResult, ReservationResult reservationResult) {
        return PaymentCompletedCriteria.builder()
            .paymentId(paymentResult.id())
            .userId(paymentResult.userId())
            .concertId(reservationResult.concertScheduleResult().concertId())
            .concertScheduleId(reservationResult.concertScheduleResult().concertScheduleId())
            .seatId(reservationResult.concertSeatResult().seatId())
            .reservationId(paymentResult.reservationId())
            .paymentAmount(reservationResult.price())
            .paymentAt(paymentResult.createdAt())
            .build();
    }

    public PaymentCompletedEvent toEvent() {
        return PaymentCompletedEvent.builder()
                .paymentId(paymentId)
                .userId(userId)
                .concertId(concertId)
                .concertScheduleId(concertScheduleId)
                .seatId(seatId)
                .reservationId(reservationId)
                .paymentAmount(paymentAmount)
                .paymentAt(paymentAt)
                .build();
    }
}
