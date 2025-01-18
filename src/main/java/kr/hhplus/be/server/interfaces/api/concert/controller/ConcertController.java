package kr.hhplus.be.server.interfaces.api.concert.controller;

import kr.hhplus.be.server.domain.concert.dto.ConcertSchedulesResult;
import kr.hhplus.be.server.domain.concert.dto.ConcertSeatsResult;
import kr.hhplus.be.server.domain.concert.service.ConcertService;
import kr.hhplus.be.server.interfaces.api.common.dto.response.BaseResponse;
import kr.hhplus.be.server.interfaces.api.common.dto.response.PaginationData;
import kr.hhplus.be.server.interfaces.api.common.dto.response.PaginationResponse;
import kr.hhplus.be.server.interfaces.api.concert.controller.apidocs.ConcertApiDocs;
import kr.hhplus.be.server.interfaces.api.concert.dto.ConcertScheduleDateResponse;
import kr.hhplus.be.server.interfaces.api.concert.dto.SeatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/concerts")
@RequiredArgsConstructor
@Validated
public class ConcertController implements ConcertApiDocs {
    private final ConcertService concertService;

    @Override
    @GetMapping("/{concert-id}/dates")
    public ResponseEntity<BaseResponse<PaginationData<ConcertScheduleDateResponse>>> getAvailableDates(
            @PathVariable("concert-id") Long concertId,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestHeader("X-Queue-Token") String token) {

        ConcertSchedulesResult result = concertService.getConcertSchedules(concertId, Optional.of(offset), Optional.of(limit));
        List<ConcertScheduleDateResponse> res = result.concertSchedules().stream()
                .map(ConcertScheduleDateResponse::fromDomainDto)
                .toList();
        return ResponseEntity.ok(PaginationResponse.of(res, result.offset(), result.limit(), result.total()));
    }

    @Override
    @GetMapping("/{concert-id}/dates/{date}/seats")
    public ResponseEntity<BaseResponse<PaginationData<SeatResponse>>> getAvailableSeats(
            @PathVariable("concert-id") Long concertId,
            @PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "50") int limit,
            @RequestHeader("X-Queue-Token") String token) {

        ConcertSeatsResult result = concertService.getSeatsByConcertIdAndEventDate(concertId, date, Optional.of(offset), Optional.of(limit));
        List<SeatResponse> res = result.seats().stream()
                .map(SeatResponse::fromDomainDto)
                .toList();

        return ResponseEntity.ok(PaginationResponse.of(res, result.offset(), result.limit(), result.total()));
    }
}
