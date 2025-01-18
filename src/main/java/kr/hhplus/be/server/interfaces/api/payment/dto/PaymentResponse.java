package kr.hhplus.be.server.interfaces.api.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.concert.dto.ConcertScheduleResult;
import kr.hhplus.be.server.domain.concert.dto.ConcertSeatResult;
import kr.hhplus.be.server.domain.reservation.dto.ReservationResult;
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
    private long seatId;

    @Schema(description = "좌석 번호", example = "A1")
    private String seatNo;

    @Schema(description = "결제 가격", example = "50000")
    private int price;

    @Schema(description = "콘서트 일시", example = "2025-01-31T18:00:00")
    private LocalDateTime concertDateTime;

    @Schema(description = "결제 상태", example = "CONFIRMED")
    private ReservationStatus status;

    @Schema(description = "예약 요청 일시", example = "2025-01-01T17:50:00")
    private LocalDateTime reservedAt;

    @Schema(description = "임시 예약 만료 일시", example = "2025-01-01T17:55:00")
    private LocalDateTime tempReservationExpiredAt;

    @Schema(description = "결제 완료 일시", example = "2025-01-01T17:53:00")
    private LocalDateTime confirmedAt;

    public static PaymentResponse fromDomainDto(ReservationResult dto) {
        ConcertScheduleResult concertSchedule = dto.concertScheduleResult();
        ConcertSeatResult concertSeat = dto.concertSeatResult();

        return PaymentResponse.builder()
                .reservationId(dto.reservationId())
                .userId(dto.userId())
                .concertId(concertSchedule.concertId())
                .concertScheduleId(concertSchedule.concertScheduleId())
                .seatId(concertSeat.seatId())
                .seatNo(concertSeat.seatNo())
                .price(dto.price())
                .concertDateTime(concertSchedule.eventDateTime())
                .status(dto.status())
                .reservedAt(dto.reservedAt())
                .tempReservationExpiredAt(dto.tempReservationExpiredAt())
                .confirmedAt(dto.confirmedAt())
                .build();
    }
}
