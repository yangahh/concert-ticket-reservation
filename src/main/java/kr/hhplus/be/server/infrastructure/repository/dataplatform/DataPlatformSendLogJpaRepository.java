package kr.hhplus.be.server.infrastructure.repository.dataplatform;

import kr.hhplus.be.server.domain.dataplatform.entity.DataPlatformSendLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataPlatformSendLogJpaRepository extends JpaRepository<DataPlatformSendLog, Long> {
    boolean existsByPaymentId(Long paymentId);
}
