package kr.hhplus.be.server.interfaces.api.payment.controller;

import jakarta.validation.Valid;
import kr.hhplus.be.server.interfaces.api.common.dto.response.BaseResponse;
import kr.hhplus.be.server.interfaces.api.payment.dto.PaymentRequest;
import kr.hhplus.be.server.interfaces.api.payment.dto.PaymentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    @PostMapping
    public ResponseEntity<BaseResponse<PaymentResponse>> processPayment(
            @RequestBody @Valid PaymentRequest request) {

        PaymentResponse res = PaymentResponse.builder()
                        .reservationId(request.getReservationId())
                        .userId(10L)
                        .concertId(101L)
                        .concertScheduleId(222L)
                        .concertDate("2025-02-10 18:00:00")
                        .seatId(501L)
                        .seatNo("A1")
                        .price(50000)
                        .confirmedAt(LocalDateTime.now())
                        .build();
        return ResponseEntity.ok(BaseResponse.ok(res));
    }
}
