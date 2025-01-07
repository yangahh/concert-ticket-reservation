package kr.hhplus.be.server.interfaces.api.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.reservation.vo.ReservationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReservationResponse {
    private Long reservationId;
    private Long userId;
    private Long concertId;
    private Long concertScheduleId;
    private Long seatId;

    @Schema(description = "좌석 번호", example = "A1")
    private String seatNo;

    @Schema(description = "결제 가격", example = "50000")
    private int price;

    @Schema(description = "콘서트 일시", example = "2025-01-31T18:00:00")
    private LocalDateTime concertDateTime;

    @Schema(description = "예약 상태", example = "PENDING_PAYMENT")
    private ReservationStatus status;

    @Schema(description = "예약 요청 일시", example = "2025-01-01T17:50:00")
    private LocalDateTime reservedAt;

    @Schema(description = "임시 예약 만료 일시", example = "2025-01-01T17:55:00")
    private LocalDateTime tempReservationExpiredAt;

    @Schema(description = "예약 확정 일시", example = "null")
    private LocalDateTime confirmedAt;
}
