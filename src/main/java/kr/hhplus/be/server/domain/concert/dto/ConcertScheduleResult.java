package kr.hhplus.be.server.domain.concert.dto;

import kr.hhplus.be.server.domain.concert.entity.ConcertSchedule;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ConcertScheduleResult(
        long concertId,
        long concertScheduleId,
        String concertName,
        LocalDateTime eventDateTime
) {
    public static ConcertScheduleResult fromEntity(ConcertSchedule entity) {
        return ConcertScheduleResult.builder()
                .concertId(entity.getConcert().getId())
                .concertScheduleId(entity.getId())
                .concertName(entity.getConcert().getTitle())
                .eventDateTime(entity.getEventDate())
                .build();
    }
}
