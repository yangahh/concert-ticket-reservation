package kr.hhplus.be.server.interfaces.api.point.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.interfaces.api.common.dto.response.ApiResponse;
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
    public ResponseEntity<ApiResponse<PointResponse>> chargePoints(@RequestBody @Valid PointRequest request) {
        PointResponse response = PointResponse.builder()
                .userId(1L)
                .balance(50000L)
                .build();
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PointResponse>> getBalance(@RequestParam("user_id") @NotNull @Positive Long userId) {
        PointResponse response = PointResponse.builder()
                .userId(1L)
                .balance(50000L)
                .build();
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
