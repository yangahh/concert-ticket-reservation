package kr.hhplus.be.server.application.queuetoken.scheduler;

import kr.hhplus.be.server.domain.queuetoken.service.QueueTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
public class ActivateTokenScheduler {
    private final QueueTokenService queueTokenService;

    @Scheduled(cron = "* * * * * *")
    public void execute() {
        queueTokenService.activateTokens();
        log.info("ActivateTokenScheduler is running at {}", LocalDateTime.now());
    }
}
