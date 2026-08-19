package kr.hhplus.be.server.domain.payment.event;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PaymentCompletedEvent (
    Long paymentId,
    Long userId,
    Long concertId,
    Long concertScheduleId,
    Long seatId,
    Long reservationId,
    int paymentAmount,
    LocalDateTime paymentAt

) {}
