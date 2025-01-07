package kr.hhplus.be.server.interfaces.api.queuetoken.controller.apidocs;

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
import kr.hhplus.be.server.interfaces.api.queuetoken.dto.QueuePositionResponse;
import kr.hhplus.be.server.interfaces.api.queuetoken.dto.QueueTokenRequest;
import kr.hhplus.be.server.interfaces.api.queuetoken.dto.QueueTokenResponse;
import kr.hhplus.be.server.utils.regexp.Patterns;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import static kr.hhplus.be.server.interfaces.api.common.exception.message.ExceptionMessage.INVALID_TOKEN_FORMAT;

@Tag(name = "Queue")
public interface QueueTokenApiDocs {
    @Operation(summary = "대기열 토큰 발급", description = "콘서트 예약 페이지 진입 시 대기열 토큰(waiting token)을 발급합니다.")
    @ApiResponse(responseCode = "201", description = "CREATED")
    @ApiResponse(responseCode = "404", description = "NOT_FOUND", content = @Content( schema = @Schema(implementation = ErrorResponse.class, example = "{\"statusCode\":404,\"message\":\"해당 사용자를 찾을 수 없습니다.\"}")))
    ResponseEntity<BaseResponse<QueueTokenResponse>> issueWaitingToken(@RequestBody @Valid QueueTokenRequest request);

    @Operation(summary = "대기열 순번 조회", description = "대기열 통과까지 남은 순번을 조회합니다.")
    @Parameter(name = "token", description = "발급된 대기열 토큰(UUID 형식)", required = true, example = "157aadfa-96bb-4b1a-8127-d6e3dd31d72b", in = ParameterIn.QUERY)
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400", description = "BAD_REQUEST", content = @Content( schema = @Schema(implementation = ErrorResponse.class, example = "{\"statusCode\":400,\"message\":\"잘못된 형식의 토큰값입니다.\"}")))
    ResponseEntity<BaseResponse<QueuePositionResponse>> getWaitingTokenPosition(
            @RequestParam("token") @Pattern(regexp = Patterns.UUID, message = INVALID_TOKEN_FORMAT) String token);
}
