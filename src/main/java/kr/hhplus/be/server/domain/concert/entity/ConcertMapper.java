package kr.hhplus.be.server.domain.concert.entity;

import kr.hhplus.be.server.domain.concert.dto.ReservationSeatInfo;

public class ConcertMapper {
    public static Concert mapToEntityFromInfoDto(ReservationSeatInfo dto) {
        return new Concert(dto.concertId(), dto.concertName());
    }

}
