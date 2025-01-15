package kr.hhplus.be.server.interfaces.api.point.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.domain.point.dto.PointResult;
import kr.hhplus.be.server.domain.point.service.PointService;
import kr.hhplus.be.server.interfaces.api.common.dto.response.BaseResponse;
import kr.hhplus.be.server.interfaces.api.point.controller.apidocs.PointApiDocs;
import kr.hhplus.be.server.interfaces.api.point.dto.PointRequest;
import kr.hhplus.be.server.interfaces.api.point.dto.PointResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/points")
@Validated
@RequiredArgsConstructor
public class PointController implements PointApiDocs {
    private final PointService pointService;

    @PostMapping
    public ResponseEntity<BaseResponse<PointResponse>> chargePoints(@RequestBody @Valid PointRequest request) {
        PointResult result = pointService.chargePoint(request.getUserId(), request.getAmount());
        return ResponseEntity.ok(BaseResponse.ok(PointResponse.fromDomainDto(result)));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PointResponse>> getBalance(@RequestParam("user_id") @Positive Long userId) {
        PointResult result = pointService.getUserPoint(userId);
        return ResponseEntity.ok(BaseResponse.ok(PointResponse.fromDomainDto(result)));
    }
}
