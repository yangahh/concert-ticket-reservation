package kr.hhplus.be.server.domain.concert.dto;

import kr.hhplus.be.server.domain.concert.entity.Seat;
import lombok.Builder;

@Builder
public record ConcertSeatResult (
        long concertScheduleId,
        long seatId,
        String seatNo,
        boolean isAvailable,
        int price
) {
    public static ConcertSeatResult fromEntity(Seat seat) {
        return ConcertSeatResult.builder()
                .concertScheduleId(seat.getConcertSchedule().getId())
                .seatId(seat.getId())
                .seatNo(seat.getSeatNo())
                .isAvailable(seat.getIsAvailable())
                .price(seat.getPrice())
                .build();
    }
}
