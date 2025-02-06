package kr.hhplus.be.server.interfaces.api.concert.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.concert.dto.ConcertResult;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ConcertResponse {
    @Schema(description = "concert ID", example = "1")
    private long concertId;

    @Schema(description = "콘서트 제목", example = "2024 연말 콘서트")
    private String concertName;

    @Schema(description = "예약 오픈 일시", example = "2024-12-31T20:00:00")
    private LocalDateTime reservationOpenDateTime;


    public static ConcertResponse fromDomainDto(ConcertResult concertResult) {
        return new ConcertResponse(concertResult.getConcertId(), concertResult.getConcertName(), concertResult.getReservationOpenDateTime());
    }
}
