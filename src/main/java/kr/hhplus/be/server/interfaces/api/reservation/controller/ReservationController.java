package kr.hhplus.be.server.interfaces.api.reservation.controller;

import jakarta.validation.Valid;
import kr.hhplus.be.server.application.reservation.usecase.ReservationUseCase;
import kr.hhplus.be.server.domain.reservation.dto.ReservationResult;
import kr.hhplus.be.server.interfaces.api.common.dto.response.BaseResponse;
import kr.hhplus.be.server.interfaces.api.reservation.controller.apidocs.ReservationApiDocs;
import kr.hhplus.be.server.interfaces.api.reservation.dto.ReservationRequest;
import kr.hhplus.be.server.interfaces.api.reservation.dto.ReservationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
@Validated
@RequiredArgsConstructor
public class ReservationController implements ReservationApiDocs {
    private final ReservationUseCase reservationUseCase;

    @PostMapping
    public ResponseEntity<BaseResponse<ReservationResponse>> reserveSeat(
            @RequestBody @Valid ReservationRequest request,
            @RequestHeader("X-Queue-Token") String token) {

        ReservationResult result = reservationUseCase.makeTempReservation(request.getUserId(), request.getSeatId());
        return ResponseEntity.ok(BaseResponse.ok(ReservationResponse.fromDomainDto(result)));
    }
}
