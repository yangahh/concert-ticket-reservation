package kr.hhplus.be.server.interfaces.api.queuetoken.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import kr.hhplus.be.server.domain.queuetoken.service.QueueTokenService;
import kr.hhplus.be.server.interfaces.api.common.dto.response.ApiResponse;
import kr.hhplus.be.server.interfaces.api.queuetoken.dto.QueuePositionResponse;
import kr.hhplus.be.server.interfaces.api.queuetoken.dto.QueueTokenRequest;
import kr.hhplus.be.server.interfaces.api.queuetoken.dto.QueueTokenResponse;
import kr.hhplus.be.server.utils.regexp.Patterns;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/queue")
@RequiredArgsConstructor
@Validated
public class QueueTokenController {
    private final QueueTokenService queueTokenService;

    @PostMapping("/token")
    public ResponseEntity<ApiResponse<QueueTokenResponse>> issueWaitingToken(@RequestBody @Valid QueueTokenRequest request) {
        return ResponseEntity.ok(ApiResponse.created(QueueTokenResponse.of(UUID.randomUUID().toString(), false)));
    }

    @GetMapping("/position")
    public ResponseEntity<ApiResponse<QueuePositionResponse>> getWaitingTokenPosition(
            @RequestParam("token") @Pattern(regexp = Patterns.UUID) String token) {
        return ResponseEntity.ok(ApiResponse.ok(QueuePositionResponse.of(57L, true)));
    }
}
