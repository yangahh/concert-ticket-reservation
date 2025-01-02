package kr.hhplus.be.server.interfaces.api.payment.dto;

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
    private String concertDate;
    private long seatId;
    private String seatNo;
    private int price;
    private LocalDateTime confirmedAt;
}
