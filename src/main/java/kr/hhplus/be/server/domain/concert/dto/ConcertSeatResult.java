package kr.hhplus.be.server.domain.concert.dto;

import kr.hhplus.be.server.domain.concert.entity.Seat;
import lombok.Builder;

@Builder
public record ConcertSeatResult (
        long concertId,
        long concertScheduleId,
        long seatId,
        String seatNo,
        boolean isAvailable
) {
    public static ConcertSeatResult fromEntity(Seat seat) {
        return ConcertSeatResult.builder()
                .concertId(seat.getConcertSchedule().getConcert().getId())
                .concertScheduleId(seat.getConcertSchedule().getId())
                .seatId(seat.getId())
                .seatNo(seat.getSeatNo())
                .isAvailable(seat.getIsAvailable())
                .build();
    }
}
