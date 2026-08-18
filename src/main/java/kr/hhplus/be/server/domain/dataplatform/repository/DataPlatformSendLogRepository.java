package kr.hhplus.be.server.domain.dataplatform.repository;

import kr.hhplus.be.server.domain.dataplatform.entity.DataPlatformSendLog;

public interface DataPlatformSendLogRepository {
    boolean existsByPaymentId(Long paymentId);

    DataPlatformSendLog save(DataPlatformSendLog dataPlatformSendLog);
}
