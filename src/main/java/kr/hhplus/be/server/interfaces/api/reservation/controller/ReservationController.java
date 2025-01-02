package kr.hhplus.be.server.interfaces.api.reservation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import kr.hhplus.be.server.domain.reservation.vo.ReservationStatus;
import kr.hhplus.be.server.interfaces.api.common.dto.response.ApiResponse;
import kr.hhplus.be.server.interfaces.api.reservation.dto.ReservationRequest;
import kr.hhplus.be.server.interfaces.api.reservation.dto.ReservationResponse;
import kr.hhplus.be.server.utils.regexp.Patterns;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
@Validated
public class ReservationController {
    @PostMapping
    public ResponseEntity<ApiResponse<ReservationResponse>> reserveSeat(
            @RequestBody @Valid ReservationRequest request,
            @RequestHeader("token") @NotNull @Pattern(regexp = Patterns.UUID) String token) {

        ReservationResponse reservation = ReservationResponse.builder()
                .reservationId(1001L)
                .userId(request.getUserId())
                .concertId(101L)
                .concertScheduleId(222L)
                .concertDate("2025-02-10 18:00:00")
                .seatId(request.getSeatId())
                .seatNo("A1")
                .reservationStatus(ReservationStatus.PENDING_PAYMENT)
                .price(50000)
                .reservedAt("2025-01-01 00:00:00")
                .tempReservationExpiredAt("2025-01-01 00:05:00")
                .build();
        return ResponseEntity.ok(ApiResponse.ok(reservation));
    }
}
