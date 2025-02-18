package kr.hhplus.be.server.domain.payment.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.entity.BaseEntity;
import kr.hhplus.be.server.domain.payment.dto.PaymentResult;
import kr.hhplus.be.server.domain.payment.vo.PaymentStatus;
import kr.hhplus.be.server.domain.reservation.entity.Reservation;
import kr.hhplus.be.server.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payment",
    indexes = {
        @Index(name = "idx_payment_reservation_id", columnList = "reservation_id"),
    })
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "reservation_id", nullable = false)
    private Long reservationId;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status = PaymentStatus.IN_PROGRESS;

    @Builder(access = AccessLevel.PROTECTED)
    Payment(Long userId, Long reservationId, Integer amount, PaymentStatus status) {
        this.userId = userId;
        this.reservationId = reservationId;
        this.amount = amount;
        this.status = status;
    }

    public static Payment create(Long userId, Long reservationId, Integer amount) {
        return Payment.builder()
            .userId(userId)
            .reservationId(reservationId)
            .amount(amount)
            .status(PaymentStatus.IN_PROGRESS)
            .build();
    }
}
