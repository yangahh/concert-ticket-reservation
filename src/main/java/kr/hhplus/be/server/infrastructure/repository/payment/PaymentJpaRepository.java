package kr.hhplus.be.server.infrastructure.repository.payment;

import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.vo.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByReservationIdAndStatus(Long reservationId, PaymentStatus status);
}
