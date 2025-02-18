package kr.hhplus.be.server.domain.payment.event;

import kr.hhplus.be.server.application.payment.dto.PaymentCompletedCriteria;
import kr.hhplus.be.server.domain.concert.entity.Concert;
import kr.hhplus.be.server.domain.concert.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.entity.Seat;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.reservation.dto.ReservationResult;
import kr.hhplus.be.server.domain.reservation.entity.Reservation;
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

) {
    public static PaymentCompletedEvent from (PaymentCompletedCriteria payment) {
        return PaymentCompletedEvent.builder()
            .paymentId(payment.paymentId())
            .userId(payment.userId())
            .concertId(payment.concertId())
            .concertScheduleId(payment.concertScheduleId())
            .seatId(payment.seatId())
            .reservationId(payment.reservationId())
            .paymentAmount(payment.paymentAmount())
            .paymentAt(payment.paymentAt())
            .build();
    }
}
