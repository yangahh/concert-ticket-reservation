package kr.hhplus.be.server.interfaces.api.payment.controller;

import jakarta.validation.Valid;
import kr.hhplus.be.server.application.payment.usecase.PaymentUseCase;
import kr.hhplus.be.server.domain.reservation.dto.ReservationResult;
import kr.hhplus.be.server.interfaces.api.common.dto.response.BaseResponse;
import kr.hhplus.be.server.interfaces.api.payment.controller.apidocs.PaymentApiDocs;
import kr.hhplus.be.server.interfaces.api.payment.dto.PaymentRequest;
import kr.hhplus.be.server.interfaces.api.payment.dto.PaymentResponse;
import kr.hhplus.be.server.interfaces.api.queuetoken.dto.QueueTokenCoreInfo;
import kr.hhplus.be.server.interfaces.utils.queuetoken.QueueTokenDecoder;
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
        QueueTokenCoreInfo decodedToken = QueueTokenDecoder.base64DecodeToken(token);
        ReservationResult result = paymentUseCase.payForReservation(request.getReservationId(), decodedToken.tokenUuid());
        return ResponseEntity.ok(BaseResponse.ok(PaymentResponse.fromDomainDto(result)));
    }
}
