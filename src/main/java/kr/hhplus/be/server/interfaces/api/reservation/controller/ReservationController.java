package kr.hhplus.be.server.interfaces.api.reservation.controller;

import jakarta.validation.Valid;
import kr.hhplus.be.server.domain.reservation.vo.ReservationStatus;
import kr.hhplus.be.server.interfaces.api.common.dto.response.BaseResponse;
import kr.hhplus.be.server.interfaces.api.reservation.controller.apidocs.ReservationApiDocs;
import kr.hhplus.be.server.interfaces.api.reservation.dto.ReservationRequest;
import kr.hhplus.be.server.interfaces.api.reservation.dto.ReservationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/reservations")
@Validated
public class ReservationController implements ReservationApiDocs {

    @PostMapping
    public ResponseEntity<BaseResponse<ReservationResponse>> reserveSeat(
            @RequestBody @Valid ReservationRequest request,
            @RequestHeader("X-Queue-Token") String token) {

        ReservationResponse reservation = ReservationResponse.builder()
                .reservationId(1001L)
                .userId(request.getUserId())
                .concertId(101L)
                .concertScheduleId(222L)
                .concertDateTime(LocalDateTime.of(2025, 1, 1, 18, 0, 0))
                .seatId(request.getSeatId())
                .seatNo("A1")
                .status(ReservationStatus.PENDING_PAYMENT)
                .price(50000)
                .reservedAt(LocalDateTime.now().minusMinutes(3))
                .tempReservationExpiredAt(LocalDateTime.now().plusMinutes(2))
                .build();
        return ResponseEntity.ok(BaseResponse.ok(reservation));
    }
}
