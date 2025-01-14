package kr.hhplus.be.server.interfaces.api.point.controller.apidocs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.interfaces.api.common.dto.response.BaseResponse;
import kr.hhplus.be.server.interfaces.api.common.dto.response.ErrorResponse;
import kr.hhplus.be.server.interfaces.api.point.dto.PointRequest;
import kr.hhplus.be.server.interfaces.api.point.dto.PointResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Point")
public interface PointApiDocs {

    @Operation(summary = "포인트 충전", description = "특정 사용자의 포인트를 충전합니다.")
    @ApiResponse(responseCode = "200", description = "포인트 충전 성공")
    ResponseEntity<BaseResponse<PointResponse>> chargePoints(@RequestBody @Valid PointRequest request);

    @Operation(summary = "포인트 조회", description = "특정 사용자의 포인트를 조회합니다.")
    @Parameter(name = "user_id", description = "사용자 ID", required = true, example = "1", in = ParameterIn.QUERY)
    @ApiResponse(responseCode = "200", description = "포인트 조회 성공")
    @ApiResponse(responseCode = "404", description = "NOT_FOUND", content = @Content( schema = @Schema(implementation = ErrorResponse.class, example = "{\"statusCode\":404,\"message\":\"해당 사용자를 찾을 수 없습니다.\"}")))
    ResponseEntity<BaseResponse<PointResponse>> getBalance(@RequestParam("user_id") @Positive Long userId);
}
