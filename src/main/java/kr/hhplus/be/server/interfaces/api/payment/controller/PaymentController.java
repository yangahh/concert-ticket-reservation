package kr.hhplus.be.server.interfaces.api.payment.controller;

import jakarta.validation.Valid;
import kr.hhplus.be.server.application.payment.usecase.PaymentUseCase;
import kr.hhplus.be.server.domain.reservation.dto.ReservationResult;
import kr.hhplus.be.server.interfaces.api.common.dto.response.BaseResponse;
import kr.hhplus.be.server.interfaces.api.payment.controller.apidocs.PaymentApiDocs;
import kr.hhplus.be.server.interfaces.api.payment.dto.PaymentRequest;
import kr.hhplus.be.server.interfaces.api.payment.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
@Validated
@RequiredArgsConstructor
public class PaymentController implements PaymentApiDocs {
    private final PaymentUseCase paymentUseCase;

    @PostMapping
    public ResponseEntity<BaseResponse<PaymentResponse>> processPayment(
            @RequestBody @Valid PaymentRequest request,
            @RequestHeader("X-Queue-Token") String token) {
        ReservationResult result = paymentUseCase.payForReservation(request.getReservationId(), UUID.fromString(token));
        return ResponseEntity.ok(BaseResponse.ok(PaymentResponse.fromDomainDto(result)));
    }
}
