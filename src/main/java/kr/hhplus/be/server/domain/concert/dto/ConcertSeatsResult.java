package kr.hhplus.be.server.domain.concert.dto;

import kr.hhplus.be.server.domain.concert.entity.Seat;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
public record ConcertSeatsResult(
        List<ConcertSeatResult> seats,
        long total,
        int offset,
        int limit
) {
    public static ConcertSeatsResult fromPage(Page<Seat> pageData) {
        List<ConcertSeatResult> seats = pageData.getContent().stream()
                .map(ConcertSeatResult::fromEntity)
                .toList();
        return ConcertSeatsResult.builder()
                .seats(seats)
                .total(pageData.getTotalElements())
                .offset(pageData.getNumber() * pageData.getSize())
                .limit(pageData.getSize())
                .build();
    }
}
