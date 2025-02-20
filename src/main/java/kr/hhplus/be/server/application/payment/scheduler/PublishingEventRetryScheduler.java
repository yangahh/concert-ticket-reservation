package kr.hhplus.be.server.application.payment.scheduler;

import kr.hhplus.be.server.domain.payment.entity.PaymentCompletedEventOutbox;
import kr.hhplus.be.server.domain.payment.messaging.PaymentMessageProducer;
import kr.hhplus.be.server.domain.payment.service.PaymentOutboxService;
import kr.hhplus.be.server.utils.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PublishingEventRetryScheduler {
    private final PaymentMessageProducer paymentMessageProducer;
    private final PaymentOutboxService paymentOutboxService;
    private final TimeProvider timeProvider;
    private static final int MAX_RETRY = 5; // 최대 재시도 횟수

    @Transactional
    @Scheduled(fixedDelay = 5000) // 5초마다 실행
    public void retryFailedPublishing() {

        List<PaymentCompletedEventOutbox> retryableEvents = paymentOutboxService.findAllUnpublishedOutboxEvents();
        log.info("[PAYMENT-SERVICE][RETRY-PUBLISH] kafka publish 재시도 시작 ({})", timeProvider.now());
        log.info("[PAYMENT-SERVICE][RETRY-PUBLISH] 재시도할 이벤트 개수: {}", retryableEvents.size());

        LocalDateTime thresholdTime = timeProvider.now().minusMinutes(1);
        for (PaymentCompletedEventOutbox event : retryableEvents) {
            if (event.getRetryCount() >= MAX_RETRY) {
                event.markAsFailed();
                log.error("[PAYMENT-SERVICE][RETRY-PUBLISH] 최대 재시도 횟수 초과 → 이벤트 FAILED 처리: id={}, paymentId={}", event.getId(), event.getPaymentId());
                continue;
            }

            // 1분간 상태가 변하지 않은 이벤트만 재시도
            if (event.getCreatedAt().isAfter(thresholdTime)) {
                log.info("[PAYMENT-SERVICE][RETRY-PUBLISH] 1분간 상태가 변하지 않았음 → 이벤트 발행 재시도: id={}, paymentId={}", event.getId(), event.getPaymentId());
                paymentMessageProducer.publishPaymentCompetedMessage(event.getPayload());
                event.increaseRetryCount();
            }

        }
        paymentOutboxService.savePaymentCompletedEventOutboxAll(retryableEvents);
        log.info("[PAYMENT-SERVICE][RETRY-PUBLISH] kafka publish 재시도 종료 ({})", timeProvider.now());
    }
}
