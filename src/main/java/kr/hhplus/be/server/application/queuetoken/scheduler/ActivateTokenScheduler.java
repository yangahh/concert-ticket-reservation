package kr.hhplus.be.server.application.queuetoken.scheduler;

import kr.hhplus.be.server.domain.queuetoken.service.QueueTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActivateTokenScheduler {
    private final QueueTokenService queueTokenService;

    @Scheduled(fixedDelay = 3000)  // 이전 실행이 끝나고 3초 후 실행
    public void execute() {
        queueTokenService.activateTokens();
        log.info("ActivateTokenScheduler is running at {}", LocalDateTime.now());
    }
}
