package kr.hhplus.be.server.interfaces.api.concert.controller.apidocs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import kr.hhplus.be.server.interfaces.api.common.dto.response.BaseResponse;
import kr.hhplus.be.server.interfaces.api.common.dto.response.ErrorResponse;
import kr.hhplus.be.server.interfaces.api.common.dto.response.PaginationData;
import kr.hhplus.be.server.interfaces.api.concert.dto.ConcertScheduleDateResponse;
import kr.hhplus.be.server.interfaces.api.concert.dto.SeatResponse;
import kr.hhplus.be.server.utils.regexp.Patterns;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

import static kr.hhplus.be.server.interfaces.api.common.exception.message.ExceptionMessage.INVALID_TOKEN_FORMAT;

@Tag(name = "Concert")
public interface ConcertApiDocs {

    @Operation(summary = "예약 가능한 날짜 목록 조회", description = "특정 콘서트의 예약 가능한 날짜 목록을 조회합니다.")
    @Parameters({
            @Parameter(name = "offset", description = "페이지 시작 인덱스", required = false, example = "0", in = ParameterIn.QUERY),
            @Parameter(name = "limit", description = "페이지 당 조회 개수", required = false, example = "10", in = ParameterIn.QUERY),
            @Parameter(name = "token", description = "발급된 대기열 토큰(UUID 형식)", required = true, example = "157aadfa-96bb-4b1a-8127-d6e3dd31d72b", in = ParameterIn.HEADER),
            @Parameter(name = "concert-id", description = "콘서트 ID", required = true, example = "1", in = ParameterIn.PATH)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "NOT_FOUND", content = @Content(schema = @Schema(implementation = ErrorResponse.class, example = "{\"statusCode\":404,\"message\":\"해당 콘서트를 찾을 수 없습니다.\"}"))),
            @ApiResponse(responseCode = "401", description = "INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ErrorResponse.class, example = "{\"statusCode\":401,\"message\":\"유효하지 않는 토큰입니다.\"}")))
    })
    ResponseEntity<BaseResponse<PaginationData<ConcertScheduleDateResponse>>> getAvailableDates(
            @PathVariable("concert-id") Long concertId,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestHeader("token") @Pattern(regexp = Patterns.UUID, message = INVALID_TOKEN_FORMAT) String token);


    @Operation(summary = "특정 날짜의 좌석 조회", description = "특정 콘서트의 특정 날짜의 좌석을 조회합니다.")
    @Parameters({
            @Parameter(name = "offset", description = "페이지 시작 인덱스", required = false, example = "0", in = ParameterIn.QUERY),
            @Parameter(name = "limit", description = "페이지 당 조회 개수", required = false, example = "50", in = ParameterIn.QUERY),
            @Parameter(name = "token", description = "발급된 대기열 토큰(UUID 형식)", required = true, example = "157aadfa-96bb-4b1a-8127-d6e3dd31d72b", in = ParameterIn.HEADER),
            @Parameter(name = "concert-id", description = "콘서트 ID", required = true, example = "1", in = ParameterIn.PATH),
            @Parameter(name = "date", description = "날짜", required = true, example = "2025-01-01", in = ParameterIn.PATH)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "NOT_FOUND", content = @Content(schema = @Schema(implementation = ErrorResponse.class, example = "{\"statusCode\":404,\"message\":\"해당 콘서트를 찾을 수 없습니다.\"}"))),
            @ApiResponse(responseCode = "401", description = "INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ErrorResponse.class, example = "{\"statusCode\":401,\"message\":\"유효하지 않는 토큰입니다.\"}")))
    })
    ResponseEntity<BaseResponse<PaginationData<SeatResponse>>> getAvailableSeats(
            @PathVariable("concert-id") Long concertId,
            @PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "50") int limit,
            @RequestHeader("token") @Pattern(regexp = Patterns.UUID, message = INVALID_TOKEN_FORMAT) String token);
}
