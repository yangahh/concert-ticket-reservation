package kr.hhplus.be.server.domain.concert.entity;

import kr.hhplus.be.server.domain.concert.dto.ReservationSeatInfo;

public class SeatMapper {
    public static Seat mapToEntityFromInfoDto(ReservationSeatInfo dto) {
        return Seat.builder()
            .id(dto.seatId())
            .concertSchedule(ConcertScheduleMapper.mapToEntityFromInfoDto(dto))
            .seatNo(dto.seatNo())
            .isAvailable(dto.isAvailable())
            .price(dto.price())
            .build();
    }
}
