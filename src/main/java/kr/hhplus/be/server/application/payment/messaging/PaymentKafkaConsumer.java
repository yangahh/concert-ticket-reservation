package kr.hhplus.be.server.application.payment.messaging;

import kr.hhplus.be.server.application.payment.usecase.PaymentUseCase;
import kr.hhplus.be.server.domain.payment.event.PaymentCompletedEvent;
import kr.hhplus.be.server.domain.payment.service.PaymentOutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static kr.hhplus.be.server.infrastructure.messaging.config.KafkaConstance.PAYMENT_COMPLETED_TOPIC;
import static kr.hhplus.be.server.infrastructure.messaging.config.KafkaConstance.PAYMENT_CONSUMER_GROUP;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentKafkaConsumer {
    private final PaymentOutboxService paymentOutboxService;

    @KafkaListener(
        topics = PAYMENT_COMPLETED_TOPIC,
        groupId = PAYMENT_CONSUMER_GROUP
    )
    public void consume(PaymentCompletedEvent event) {
        try {
            log.info("[PAYMENT-SERVICE][PaymentKafkaConsumer] 결제 처리 KafkaListener: 결제 ID - {}", event.paymentId());
            paymentOutboxService.updatePaymentCompletedEventOutboxStatus(event);
            log.info("[PAYMENT-SERVICE][PaymentKafkaConsumer] 결제 처리 완료: 결제 ID - {}", event.paymentId());
        } catch (Exception e) {
            log.error("[PAYMENT-SERVICE][PaymentKafkaConsumer] 결제 처리 실패: {}", e.getMessage(), e);
            // TODO: DLQ에 메시지 전송
        }
    }
}
