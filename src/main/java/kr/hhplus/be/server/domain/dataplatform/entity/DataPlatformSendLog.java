package kr.hhplus.be.server.domain.dataplatform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import kr.hhplus.be.server.domain.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 데이터플랫폼으로 결제 완료 데이터를 전송했음을 기록하는 로그.
 * paymentId에 유니크 제약을 걸어, Kafka 재배달/재시도로 같은 이벤트가 여러 번 들어와도
 * 실제 외부 전송은 최초 1회만 발생하도록 멱등성을 보장한다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "data_platform_send_log",
    uniqueConstraints = @UniqueConstraint(name = "uk_data_platform_send_log_payment_id", columnNames = "payment_id"))
public class DataPlatformSendLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_id", nullable = false)
    private Long paymentId;

    @Builder(access = AccessLevel.PRIVATE)
    private DataPlatformSendLog(Long paymentId) {
        this.paymentId = paymentId;
    }

    public static DataPlatformSendLog create(Long paymentId) {
        return DataPlatformSendLog.builder()
            .paymentId(paymentId)
            .build();
    }
}
