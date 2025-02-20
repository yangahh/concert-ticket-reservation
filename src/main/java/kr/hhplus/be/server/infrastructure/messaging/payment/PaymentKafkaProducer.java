package kr.hhplus.be.server.infrastructure.messaging.payment;

import kr.hhplus.be.server.domain.payment.event.PaymentCompletedEvent;
import kr.hhplus.be.server.domain.payment.messaging.PaymentMessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static kr.hhplus.be.server.infrastructure.messaging.config.KafkaConstance.PAYMENT_COMPLETED_TOPIC;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentKafkaProducer implements PaymentMessageProducer {
    private final KafkaTemplate<String, PaymentCompletedEvent> kafkaTemplate;

    @Override
    public void publishPaymentCompetedMessage(PaymentCompletedEvent event) {
        log.info("[PAYMENT-SERVICE][PaymentKafkaProducer] 결제 완료 Kafka 메세지 발행: 결제 ID - {}", event.paymentId());
        kafkaTemplate.send(PAYMENT_COMPLETED_TOPIC, event);
        log.info("[PAYMENT-SERVICE][PaymentKafkaProducer] 결제 완료 Kafka 메세지 발행 완료");
    }
}
