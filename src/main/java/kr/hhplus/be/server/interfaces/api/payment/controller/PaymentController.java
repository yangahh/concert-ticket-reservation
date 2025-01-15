package kr.hhplus.be.server.interfaces.api.payment.controller;

import jakarta.validation.Valid;
import kr.hhplus.be.server.domain.reservation.vo.ReservationStatus;
import kr.hhplus.be.server.interfaces.api.common.dto.response.BaseResponse;
import kr.hhplus.be.server.interfaces.api.payment.controller.apidocs.PaymentApiDocs;
import kr.hhplus.be.server.interfaces.api.payment.dto.PaymentRequest;
import kr.hhplus.be.server.interfaces.api.payment.dto.PaymentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/payments")
@Validated
public class PaymentController implements PaymentApiDocs {

    @PostMapping
    public ResponseEntity<BaseResponse<PaymentResponse>> processPayment(
            @RequestBody @Valid PaymentRequest request,
            @RequestHeader("X-Queue-Token") String token) {
        PaymentResponse res = PaymentResponse.builder()
                        .reservationId(request.getReservationId())
                        .userId(10L)
                        .concertId(101L)
                        .concertScheduleId(222L)
                        .concertDateTime(LocalDateTime.of(2025, 1, 1, 18, 0, 0))
                        .seatId(501L)
                        .seatNo("A1")
                        .status(ReservationStatus.CONFIRMED)
                        .price(50000)
                        .reservedAt(LocalDateTime.now().minusMinutes(3))
                        .tempReservationExpiredAt(LocalDateTime.now().plusMinutes(2))
                        .confirmedAt(LocalDateTime.now())
                        .build();
        return ResponseEntity.ok(BaseResponse.ok(res));
    }
}
