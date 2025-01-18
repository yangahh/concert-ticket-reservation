package kr.hhplus.be.server.interfaces.api.payment.controller.apidocs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.server.interfaces.api.common.dto.response.BaseResponse;
import kr.hhplus.be.server.interfaces.api.common.dto.response.ErrorResponse;
import kr.hhplus.be.server.interfaces.api.payment.dto.PaymentRequest;
import kr.hhplus.be.server.interfaces.api.payment.dto.PaymentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Payment")
public interface PaymentApiDocs {
    @Operation(summary = "좌석 결제 요청", description = "임시로 배정된 좌석을 결제하고 예약을 확정합니다.")
    @Parameter(name = "token", description = "발급된 대기열 토큰(UUID 형식)", required = true, example = "157aadfa-96bb-4b1a-8127-d6e3dd31d72b", in = ParameterIn.HEADER)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "INVALID_TOKEN", content = @Content( schema = @Schema(implementation = ErrorResponse.class, example = "{\"statusCode\":401,\"message\":\"유효하지 않는 토큰입니다.\"}"))),
            @ApiResponse(responseCode =  "422", description = "포인트 부족으로 인한 결제 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class, example = "{\"statusCode\":422,\"message\":\"잔액이 부족합니다.\"}"))),
            @ApiResponse(responseCode = "410", description = "예약 만료로 인한 결제 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class, example = "{\"statusCode\":410,\"message\":\"예약에 대한 결제 가능 시간이 만료되었습니다.\"}")))
    })
    ResponseEntity<BaseResponse<PaymentResponse>> processPayment(
            @RequestBody @Valid PaymentRequest request,
            @RequestHeader("X-Queue-Token") String token);
}
