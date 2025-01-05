package kr.hhplus.be.server.interfaces.api.reservation.dto;

import kr.hhplus.be.server.domain.reservation.vo.ReservationStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationResponse {
    private Long reservationId;
    private Long userId;
    private Long concertId;
    private Long concertScheduleId;
    private String concertDate;
    private Long seatId;
    private String seatNo;
    private ReservationStatus reservationStatus;
    private Integer price;
    private String reservedAt;
    private String tempReservationExpiredAt;
    private String confirmedAt;
}
