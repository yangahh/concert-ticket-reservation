package kr.hhplus.be.server.application.payment.eventlistener;

import kr.hhplus.be.server.domain.payment.event.PaymentCompletedEvent;
import kr.hhplus.be.server.domain.payment.messaging.PaymentMessageProducer;
import kr.hhplus.be.server.domain.payment.service.PaymentOutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {
    private final PaymentMessageProducer paymentMessageProducer;
    private final PaymentOutboxService paymentOutboxService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void saveOutbox(PaymentCompletedEvent event) {
        log.info("[PAYMENT-SERVICE][PaymentEventListener] 결제 정보 데이터 outbox 저장: 사용자 ID = {}, 결제 ID = {}, 예약 ID = {}", event.userId(), event.paymentId(), event.reservationId());
        paymentOutboxService.savePaymentCompletedEvent(event);
        log.info("[PAYMENT-SERVICE][PaymentEventListener] 결제 정보 데이터 outbox 저장 완료");
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendPaymentCompletedMessage(PaymentCompletedEvent event) {
        log.info("[PAYMENT-SERVICE][PaymentEventListener] 결제 정보 Kafka 메세지 발행: 사용자 ID = {}, 결제 ID = {}", event.userId(), event.paymentId());
        paymentMessageProducer.publishPaymentCompetedMessage(event);
        log.info("[PAYMENT-SERVICE][PaymentEventListener] 결제 정보 Kafka 메세지 발행 완료");
    }
}
