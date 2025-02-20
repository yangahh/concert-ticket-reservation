package kr.hhplus.be.server.domain.payment.service;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.common.vo.OutboxStatus;
import kr.hhplus.be.server.domain.payment.entity.PaymentCompletedEventOutbox;
import kr.hhplus.be.server.domain.payment.event.PaymentCompletedEvent;
import kr.hhplus.be.server.domain.payment.repository.PaymentOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentOutboxService {
    private final PaymentOutboxRepository paymentOutboxRepository;

    @Transactional
    public void savePaymentCompletedEvent(PaymentCompletedEvent event) {
        PaymentCompletedEventOutbox outbox = PaymentCompletedEventOutbox.create(event.paymentId(), event);
        paymentOutboxRepository.savePaymentCompletedEventOutbox(outbox);
    }

    @Transactional
    public void updatePaymentCompletedEventOutboxStatus(PaymentCompletedEvent event) {
        Optional<PaymentCompletedEventOutbox> optionalOutbox = paymentOutboxRepository.findPaymentCompletedEventOutboxByPaymentIdAndStatus(
            event.paymentId(), OutboxStatus.INIT);

        if (optionalOutbox.isEmpty()) {
            log.error("[PAYMENT-SERVICE] Outbox data of INIT status not found. paymentId = {}", event.paymentId());
            // TODO: DLQ 처리
            return;
        }
        PaymentCompletedEventOutbox outbox = optionalOutbox.get();
        outbox.markAsPublished();
        paymentOutboxRepository.savePaymentCompletedEventOutbox(outbox);
    }

    @Transactional(readOnly = true)
    public List<PaymentCompletedEventOutbox> findAllUnpublishedOutboxEvents() {
        return paymentOutboxRepository.findPaymentCompletedEventOutboxByStatus(OutboxStatus.INIT);
    }

    @Transactional
    public void savePaymentCompletedEventOutboxAll(List<PaymentCompletedEventOutbox> outbox) {
        paymentOutboxRepository.savePaymentCompletedEventOutboxAll(outbox);
    }
}
