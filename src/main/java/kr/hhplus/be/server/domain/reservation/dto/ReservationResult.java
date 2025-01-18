package kr.hhplus.be.server.domain.reservation.dto;

import kr.hhplus.be.server.domain.concert.dto.ConcertScheduleResult;
import kr.hhplus.be.server.domain.concert.dto.ConcertSeatResult;
import kr.hhplus.be.server.domain.reservation.entity.Reservation;
import kr.hhplus.be.server.domain.reservation.vo.ReservationStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReservationResult(
        long reservationId,
        long userId,
        int price,
        ReservationStatus status,
        LocalDateTime reservedAt,
        LocalDateTime tempReservationExpiredAt,
        LocalDateTime confirmedAt,

        ConcertScheduleResult concertScheduleResult,
        ConcertSeatResult concertSeatResult
) {
    public static ReservationResult fromEntity(Reservation reservation) {
        ConcertScheduleResult concertSchedule = ConcertScheduleResult.fromEntity(reservation.getSeat().getConcertSchedule());
        ConcertSeatResult concertSeat = ConcertSeatResult.fromEntity(reservation.getSeat());
        return ReservationResult.builder()
                .reservationId(reservation.getId())
                .userId(reservation.getUser().getId())
                .price(reservation.getPaymentPrice())
                .status(reservation.getStatus())
                .reservedAt(reservation.getCreatedAt())
                .tempReservationExpiredAt(reservation.getTempReservationExpiredAt())
                .confirmedAt(reservation.getConfirmedAt())
                .concertScheduleResult(concertSchedule)
                .concertSeatResult(concertSeat)
                .build();
    }

}
