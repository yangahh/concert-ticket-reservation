package kr.hhplus.be.server.interfaces.api.payment.dto;

import kr.hhplus.be.server.domain.reservation.vo.ReservationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentResponse {
    private long reservationId;
    private long userId;
    private long concertId;
    private long concertScheduleId;
    private LocalDateTime concertDateTime;
    private long seatId;
    private String seatNo;
    private ReservationStatus status;
    private int price;
    private LocalDateTime reservedAt;
    private LocalDateTime tempReservationExpiredAt;
    private LocalDateTime confirmedAt;
}
