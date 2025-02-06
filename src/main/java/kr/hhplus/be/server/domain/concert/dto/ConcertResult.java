package kr.hhplus.be.server.domain.concert.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import kr.hhplus.be.server.domain.concert.entity.Concert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConcertResult {
    long concertId;
    String concertName;
    LocalDateTime reservationOpenDateTime;

    public static ConcertResult fromEntity(Concert entity) {
        return ConcertResult.builder()
                .concertId(entity.getId())
                .concertName(entity.getTitle())
                .reservationOpenDateTime(entity.getReservationOpenDateTime())
                .build();
    }
}
