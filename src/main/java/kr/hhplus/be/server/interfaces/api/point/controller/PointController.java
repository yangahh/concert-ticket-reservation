package kr.hhplus.be.server.interfaces.api.point.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.interfaces.api.common.dto.response.BaseResponse;
import kr.hhplus.be.server.interfaces.api.point.dto.PointRequest;
import kr.hhplus.be.server.interfaces.api.point.dto.PointResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/points")
@Validated
public class PointController {
    @PostMapping
    public ResponseEntity<BaseResponse<PointResponse>> chargePoints(@RequestBody @Valid PointRequest request) {
        PointResponse response = PointResponse.builder()
                .userId(1L)
                .balance(50000L)
                .build();
        return ResponseEntity.ok(BaseResponse.ok(response));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PointResponse>> getBalance(@RequestParam("user_id") @Positive Long userId) {
        PointResponse response = PointResponse.builder()
                .userId(1L)
                .balance(50000L)
                .build();
        return ResponseEntity.ok(BaseResponse.ok(response));
    }
}
