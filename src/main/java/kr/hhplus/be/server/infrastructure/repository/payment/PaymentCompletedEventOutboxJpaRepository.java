package kr.hhplus.be.server.infrastructure.repository.payment;

import kr.hhplus.be.server.domain.common.vo.OutboxStatus;
import kr.hhplus.be.server.domain.payment.entity.PaymentCompletedEventOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentCompletedEventOutboxJpaRepository extends JpaRepository<PaymentCompletedEventOutbox, Long> {
    List<PaymentCompletedEventOutbox> findByStatus(OutboxStatus status);

    Optional<PaymentCompletedEventOutbox> findByPaymentIdAndStatus(Long paymentId, OutboxStatus status);

    List<PaymentCompletedEventOutbox> findByPaymentId(Long paymentId);
}
