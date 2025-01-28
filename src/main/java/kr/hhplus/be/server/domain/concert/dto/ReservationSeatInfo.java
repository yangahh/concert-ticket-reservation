package kr.hhplus.be.server.domain.concert.dto;

import kr.hhplus.be.server.domain.concert.entity.Seat;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReservationSeatInfo(
        Long seatId,
        String seatNo,
        boolean isAvailable,
        LocalDateTime tempReservationExpiredAt,
        Long concertScheduleId,
        Long concertId,
        String concertName,
        LocalDateTime eventDateTime,
        int price
) {
    public static ReservationSeatInfo fromEntity(Seat seat) {
        return ReservationSeatInfo.builder()
                .seatId(seat.getId())
                .seatNo(seat.getSeatNo())
                .isAvailable(seat.getIsAvailable())
                .tempReservationExpiredAt(seat.getTempReservationExpiredAt())
                .concertScheduleId(seat.getConcertSchedule().getId())
                .concertId(seat.getConcertSchedule().getConcert().getId())
                .concertName(seat.getConcertSchedule().getConcert().getTitle())
                .eventDateTime(seat.getConcertSchedule().getEventDate())
                .price(seat.getPrice())
                .build();
    }

}
