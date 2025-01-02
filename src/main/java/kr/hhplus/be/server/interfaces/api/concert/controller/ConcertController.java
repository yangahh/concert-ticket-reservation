package kr.hhplus.be.server.interfaces.api.concert.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import kr.hhplus.be.server.domain.concert.service.ConcertService;
import kr.hhplus.be.server.domain.concert.vo.SeatStatus;
import kr.hhplus.be.server.interfaces.api.common.dto.response.ApiResponse;
import kr.hhplus.be.server.interfaces.api.concert.dto.SeatResponse;
import kr.hhplus.be.server.utils.regexp.Patterns;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/concerts")
@RequiredArgsConstructor
@Validated
public class ConcertController {
    private final ConcertService concertService;

    @GetMapping("/{concertId}/dates")
    public ResponseEntity<ApiResponse<List<String>>> getAvailableDates(
            @PathVariable("concertId") @NotNull Long concertId,
            @RequestHeader("token") @NotNull @Pattern(regexp = Patterns.UUID) String token) {
        List<String> availableDates = List.of("2025-01-01", "2025-01-02");
        ApiResponse<List<String>> response = ApiResponse.ok(availableDates);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{concertId}/dates/{date}/seats")
    public ResponseEntity<ApiResponse<List<SeatResponse>>> getAvailableSeats(
            @PathVariable("concertId") @NotNull Long concertId,
            @PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestHeader("token")  @NotNull @Pattern(regexp = Patterns.UUID) String token) {

        List<SeatResponse> availableSeats = List.of(
                SeatResponse.of(1L, "A1", SeatStatus.AVAILABLE),
                SeatResponse.of(2L, "A2", SeatStatus.AVAILABLE),
                SeatResponse.of(3L, "A3", SeatStatus.AVAILABLE)
        );

        ApiResponse<List<SeatResponse>> response = ApiResponse.ok(availableSeats);
        return ResponseEntity.ok(response);
    }
}
