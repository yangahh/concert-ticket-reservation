package kr.hhplus.be.server.interfaces.api.concert.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ConcertScheduleDateResponse {
    private long concertId;
    private long concertScheduleId;

    @Schema(description = "콘서트 일시", example = "2025-01-31T18:00:00")
    private LocalDateTime concertDateTime;

    public static ConcertScheduleDateResponse of(long concertId, long concertScheduleId, LocalDateTime concertDateTime) {
        return ConcertScheduleDateResponse.builder()
                .concertId(concertId)
                .concertScheduleId(concertScheduleId)
                .concertDateTime(concertDateTime)
                .build();
    }
}
