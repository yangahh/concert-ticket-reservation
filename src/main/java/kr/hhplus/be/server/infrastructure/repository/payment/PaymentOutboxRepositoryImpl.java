package kr.hhplus.be.server.infrastructure.repository.payment;

import kr.hhplus.be.server.domain.common.vo.OutboxStatus;
import kr.hhplus.be.server.domain.payment.entity.PaymentCompletedEventOutbox;
import kr.hhplus.be.server.domain.payment.repository.PaymentOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PaymentOutboxRepositoryImpl implements PaymentOutboxRepository {
    private final PaymentCompletedEventOutboxJpaRepository paymentCompletedEventOutboxJpaRepository;

    @Override
    public PaymentCompletedEventOutbox savePaymentCompletedEventOutbox(PaymentCompletedEventOutbox paymentCompletedEventOutbox) {
        return paymentCompletedEventOutboxJpaRepository.save(paymentCompletedEventOutbox);
    }

    @Override
    public List<PaymentCompletedEventOutbox> findPaymentCompletedEventOutboxByStatus(OutboxStatus status) {
        return paymentCompletedEventOutboxJpaRepository.findByStatus(status);
    }

    @Override
    public Optional<PaymentCompletedEventOutbox> findPaymentCompletedEventOutboxByPaymentIdAndStatus(Long paymentId, OutboxStatus status) {
        List<PaymentCompletedEventOutbox> byPaymentId = paymentCompletedEventOutboxJpaRepository.findByPaymentId(paymentId);
        for (PaymentCompletedEventOutbox outbox : byPaymentId) {
            System.out.println("outbox status = " + outbox.getStatus());
        }
        return paymentCompletedEventOutboxJpaRepository.findByPaymentIdAndStatus(paymentId, status);
    }

    @Override
    public void savePaymentCompletedEventOutboxAll(List<PaymentCompletedEventOutbox> outbox) {
        paymentCompletedEventOutboxJpaRepository.saveAll(outbox);
    }
}
