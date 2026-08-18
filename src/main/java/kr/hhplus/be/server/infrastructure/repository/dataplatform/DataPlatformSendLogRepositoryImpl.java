package kr.hhplus.be.server.infrastructure.repository.dataplatform;

import kr.hhplus.be.server.domain.dataplatform.entity.DataPlatformSendLog;
import kr.hhplus.be.server.domain.dataplatform.repository.DataPlatformSendLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DataPlatformSendLogRepositoryImpl implements DataPlatformSendLogRepository {
    private final DataPlatformSendLogJpaRepository dataPlatformSendLogJpaRepository;

    @Override
    public boolean existsByPaymentId(Long paymentId) {
        return dataPlatformSendLogJpaRepository.existsByPaymentId(paymentId);
    }

    @Override
    public DataPlatformSendLog save(DataPlatformSendLog dataPlatformSendLog) {
        return dataPlatformSendLogJpaRepository.save(dataPlatformSendLog);
    }
}
