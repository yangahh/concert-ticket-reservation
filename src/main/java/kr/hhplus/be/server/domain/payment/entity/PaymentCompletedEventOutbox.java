package kr.hhplus.be.server.domain.payment.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.entity.BaseEntity;
import kr.hhplus.be.server.domain.common.vo.OutboxStatus;
import kr.hhplus.be.server.domain.payment.event.PaymentCompletedEvent;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payment_completed_event_outbox",
    indexes = {
        @Index(name = "idx_payment_completed_event_outbox_status", columnList = "status"),
        @Index(name = "idx_payment_completed_event_outbox_payment_id", columnList = "payment_id")
})
public class PaymentCompletedEventOutbox extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_id", nullable = false)
    private Long paymentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OutboxStatus status;

    @Column(name = "payload", nullable = false, columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private PaymentCompletedEvent payload;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount;

    @Builder
    PaymentCompletedEventOutbox(Long paymentId, OutboxStatus status, PaymentCompletedEvent payload, Integer retryCount) {
        this.paymentId = paymentId;
        this.status = status;
        this.payload = payload;
        this.retryCount = retryCount;
    }

    public static PaymentCompletedEventOutbox create(Long paymentId, PaymentCompletedEvent event) {
        return PaymentCompletedEventOutbox.builder()
            .paymentId(paymentId)
            .status(OutboxStatus.INIT)
            .payload(event)
            .retryCount(0)
            .build();
    }

    public void increaseRetryCount() {
        this.retryCount++;
    }

    public void markAsInit() {
        this.status = OutboxStatus.INIT;
    }

    public void markAsPublished() {
        this.status = OutboxStatus.PUBLISHED;
    }

    public void markAsFailed() {
        this.status = OutboxStatus.FAILED;
    }
}
