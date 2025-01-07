package kr.hhplus.be.server.interfaces.api.queuetoken.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import kr.hhplus.be.server.domain.queuetoken.service.QueueTokenService;
import kr.hhplus.be.server.interfaces.api.common.dto.response.BaseResponse;
import kr.hhplus.be.server.interfaces.api.queuetoken.dto.QueuePositionResponse;
import kr.hhplus.be.server.interfaces.api.queuetoken.dto.QueueTokenRequest;
import kr.hhplus.be.server.interfaces.api.queuetoken.dto.QueueTokenResponse;
import kr.hhplus.be.server.utils.regexp.Patterns;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static kr.hhplus.be.server.interfaces.api.common.exception.message.ExceptionMessage.INVALID_TOKEN_FORMAT;

@RestController
@RequestMapping("/queue")
@RequiredArgsConstructor
@Validated
public class QueueTokenController {
    private final QueueTokenService queueTokenService;

    @PostMapping("/token")
    public ResponseEntity<BaseResponse<QueueTokenResponse>> issueWaitingToken(@RequestBody @Valid QueueTokenRequest request) {
        return new ResponseEntity<>(BaseResponse.created(QueueTokenResponse.of(UUID.randomUUID().toString(), false)), HttpStatus.CREATED);
    }

    @GetMapping("/position")
    public ResponseEntity<BaseResponse<QueuePositionResponse>> getWaitingTokenPosition(
            @RequestParam("token") @Pattern(regexp = Patterns.UUID, message = INVALID_TOKEN_FORMAT) String token) {
        return ResponseEntity.ok(BaseResponse.ok(QueuePositionResponse.of(57L, true)));
    }
}
