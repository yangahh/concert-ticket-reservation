package kr.hhplus.be.server.interfaces.api.concert.controller;

import jakarta.validation.constraints.Pattern;
import kr.hhplus.be.server.domain.concert.service.ConcertService;
import kr.hhplus.be.server.domain.concert.vo.SeatStatus;
import kr.hhplus.be.server.interfaces.api.common.dto.response.BaseResponse;
import kr.hhplus.be.server.interfaces.api.common.dto.response.PaginationData;
import kr.hhplus.be.server.interfaces.api.common.dto.response.PaginationResponse;
import kr.hhplus.be.server.interfaces.api.concert.dto.ConcertScheduleDateResponse;
import kr.hhplus.be.server.interfaces.api.concert.dto.SeatResponse;
import kr.hhplus.be.server.utils.regexp.Patterns;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static kr.hhplus.be.server.interfaces.api.common.exception.message.ExceptionMessage.INVALID_TOKEN_FORMAT;

@RestController
@RequestMapping("/concerts")
@RequiredArgsConstructor
@Validated
public class ConcertController {
    private final ConcertService concertService;

    @GetMapping("/{concert-id}/dates")
    public ResponseEntity<BaseResponse<PaginationData<ConcertScheduleDateResponse>>> getAvailableDates(
            @PathVariable("concert-id") Long concertId,
            @RequestHeader("token") @Pattern(regexp = Patterns.UUID, message = INVALID_TOKEN_FORMAT) String token) {

        List<ConcertScheduleDateResponse> concertDates = List.of(
                ConcertScheduleDateResponse.of(1L, 1L, LocalDateTime.of(2025, 1, 1, 18, 0, 0)),
                ConcertScheduleDateResponse.of(1L, 2L, LocalDateTime.of(2025, 1, 2, 18, 0, 0))
        );

        return ResponseEntity.ok(PaginationResponse.of(concertDates, 0, 10, 2L));
    }

    @GetMapping("/{concert-id}/dates/{date}/seats")
    public ResponseEntity<BaseResponse<PaginationData<SeatResponse>>> getAvailableSeats(
            @PathVariable("concert-id") Long concertId,
            @PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestHeader("token") @Pattern(regexp = Patterns.UUID, message = INVALID_TOKEN_FORMAT) String token) {

        List<SeatResponse> availableSeats = List.of(
                SeatResponse.of(1L, "A1", SeatStatus.AVAILABLE),
                SeatResponse.of(2L, "A2", SeatStatus.AVAILABLE),
                SeatResponse.of(3L, "A3", SeatStatus.AVAILABLE)
        );

        return ResponseEntity.ok(PaginationResponse.of(availableSeats, 0, 50, 50L));
    }
}
