package kr.hhplus.be.server.interfaces.api.concert.dto;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ConcertScheduleDateResponse {
    private long concertId;
    private long concertScheduleId;
    private LocalDateTime concertDateTime;

    public static ConcertScheduleDateResponse of(long concertId, long concertScheduleId, LocalDateTime concertDateTime) {
        return ConcertScheduleDateResponse.builder()
                .concertId(concertId)
                .concertScheduleId(concertScheduleId)
                .concertDateTime(concertDateTime)
                .build();
    }
}
