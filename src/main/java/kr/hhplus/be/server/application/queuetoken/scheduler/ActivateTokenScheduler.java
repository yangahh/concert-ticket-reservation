package kr.hhplus.be.server.application.queuetoken.scheduler;

import kr.hhplus.be.server.domain.concert.service.ConcertService;
import kr.hhplus.be.server.domain.queuetoken.service.QueueTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActivateTokenScheduler {
    private final QueueTokenService queueTokenService;
    private final ConcertService concertService;

    @Scheduled(fixedRate = 10000)  // 10초마다 실행
    public void execute() {
        log.info("==============================ActivateTokenScheduler================================");
        log.info("Started at {}", LocalDateTime.now());
        List<Long> allConcertIds = concertService.getAllConcertIds();
        for (Long concertId : allConcertIds) {
            log.info("For ConcertId '{}'", concertId);
            queueTokenService.activateTokens(concertId);
        }
        log.info("==============================ActivateTokenScheduler End================================");
    }
}
