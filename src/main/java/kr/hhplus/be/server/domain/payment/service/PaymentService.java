package kr.hhplus.be.server.domain.payment.service;

import kr.hhplus.be.server.domain.payment.dto.PaymentResult;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.repository.PaymentRepository;
import kr.hhplus.be.server.domain.reservation.dto.ReservationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentResult createPayment(ReservationResult reservation) {
        Payment payment = Payment.create(reservation.userId(), reservation.reservationId(), reservation.price());
        Payment saved = paymentRepository.save(payment);
        return PaymentResult.fromEntity(saved);
    }
}
