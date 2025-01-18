package kr.hhplus.be.server.domain.concert.dto;

import kr.hhplus.be.server.domain.concert.entity.ConcertSchedule;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
public record ConcertSchedulesResult(
        List<ConcertScheduleResult> concertSchedules,
        long total,
        int offset,
        int limit
) {
    public static ConcertSchedulesResult fromPage(Page<ConcertSchedule> pageData) {
        List<ConcertScheduleResult> concertSchedules = pageData.getContent().stream()
                .map(ConcertScheduleResult::fromEntity)
                .toList();
        return ConcertSchedulesResult.builder()
                .concertSchedules(concertSchedules)
                .total(pageData.getTotalElements())
                .offset(pageData.getNumber() * pageData.getSize())
                .limit(pageData.getSize())
                .build();
    }
}
