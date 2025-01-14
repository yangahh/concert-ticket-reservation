package kr.hhplus.be.server.interfaces.api.reservation.controller.apidocs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import kr.hhplus.be.server.interfaces.api.common.dto.response.BaseResponse;
import kr.hhplus.be.server.interfaces.api.common.dto.response.ErrorResponse;
import kr.hhplus.be.server.interfaces.api.reservation.dto.ReservationRequest;
import kr.hhplus.be.server.interfaces.api.reservation.dto.ReservationResponse;
import kr.hhplus.be.server.utils.regexp.Patterns;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import static kr.hhplus.be.server.interfaces.api.common.exception.message.ExceptionMessage.INVALID_TOKEN_FORMAT;

@Tag(name = "Reservation")
public interface ReservationApiDocs {

    @Operation(summary = "좌석 예약 요청", description = "선택한 좌석을 임시 예약합니다.")
    @Parameter(name = "token", description = "발급된 대기열 토큰(UUID 형식)", required = true, example = "157aadfa-96bb-4b1a-8127-d6e3dd31d72b", in = ParameterIn.HEADER)
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰", content = @Content( schema = @Schema(implementation = ErrorResponse.class, example = "{\"statusCode\":401,\"message\":\"유효하지 않는 토큰입니다.\"}")))
    @ApiResponse(responseCode = "409", description = "이미 예약된 좌석일 경우", content = @Content(schema = @Schema(implementation = ErrorResponse.class, example = "{\"statusCode\":409,\"message\":\"이미 예약된 좌석입니다.\"}")))
    ResponseEntity<BaseResponse<ReservationResponse>> reserveSeat(
            @RequestBody @Valid ReservationRequest request,
            @RequestHeader("token") @Pattern(regexp = Patterns.UUID, message = INVALID_TOKEN_FORMAT) String token);
}
