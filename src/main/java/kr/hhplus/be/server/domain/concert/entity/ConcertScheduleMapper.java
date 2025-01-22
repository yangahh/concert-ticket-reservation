package kr.hhplus.be.server.domain.concert.entity;

import kr.hhplus.be.server.domain.concert.dto.ReservationSeatInfo;

public class ConcertScheduleMapper {
    public static ConcertSchedule mapToEntityFromInfoDto(ReservationSeatInfo dto) {
        return new ConcertSchedule(dto.concertScheduleId(),
                                    ConcertMapper.mapToEntityFromInfoDto(dto),
                                    dto.eventDateTime(),
                                    null);
    }
}
