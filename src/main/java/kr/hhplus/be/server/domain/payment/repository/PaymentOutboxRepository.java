package kr.hhplus.be.server.domain.payment.repository;

import kr.hhplus.be.server.domain.common.vo.OutboxStatus;
import kr.hhplus.be.server.domain.payment.entity.PaymentCompletedEventOutbox;

import java.util.List;
import java.util.Optional;

public interface PaymentOutboxRepository {
    PaymentCompletedEventOutbox savePaymentCompletedEventOutbox(PaymentCompletedEventOutbox paymentCompletedEventOutbox);
    List<PaymentCompletedEventOutbox> findPaymentCompletedEventOutboxByStatus(OutboxStatus status);
    Optional<PaymentCompletedEventOutbox> findPaymentCompletedEventOutboxByPaymentIdAndStatus(Long paymentId, OutboxStatus status);
    void savePaymentCompletedEventOutboxAll(List<PaymentCompletedEventOutbox> outbox);

}
