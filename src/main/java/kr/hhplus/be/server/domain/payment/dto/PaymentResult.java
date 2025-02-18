package kr.hhplus.be.server.domain.payment.dto;

import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.vo.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
public record PaymentResult (
    Long id,
    Long userId,
    Long reservationId,
    Long concertId,
    Long concertScheduleId,
    Long concertSeatId,
    int amount,
    PaymentStatus status,
    LocalDateTime createdAt
){

    public static PaymentResult fromEntity(Payment payment) {
        return PaymentResult.builder()
            .id(payment.getId())
            .userId(payment.getUserId())
            .reservationId(payment.getReservationId())
            .amount(payment.getAmount())
            .status(payment.getStatus())
            .createdAt(payment.getCreatedAt())
            .build();
    }
}
